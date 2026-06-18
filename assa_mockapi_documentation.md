# ASSA ABLOY Keyless Administration — Mock REST API

A lightweight Spring Boot / Kotlin mock of the [ASSA ABLOY Keyless Administration API v2](https://api-docs.keyless.assaabloy.com/administration/v2/index.html).
It reproduces a useful subset of the real cloud API — login, users, locking devices and
permissions — so client applications (e.g. *AllesLocker*) can be developed and tested without
talking to the production Keyless platform.

The mock is intentionally simple: it stores everything in an in-memory H2 database, accepts a
single fixed set of credentials, and never communicates with real hardware. It mirrors the real
API's URL shapes, request/response JSON and status codes closely enough to be a drop-in target
during development.

---

## Table of contents

- [Tech stack](#tech-stack)
- [Running the mock](#running-the-mock)
- [Authentication model](#authentication-model)
- [Conventions](#conventions)
- [Endpoints](#endpoints)
  - [Login](#login)
  - [Users](#users)
  - [Locking devices](#locking-devices)
  - [Permissions](#permissions)
- [Domain model](#domain-model)
- [How the mock differs from the real API](#how-the-mock-differs-from-the-real-api)
- [Data the mock does *not* return through endpoints](#data-the-mock-does-not-return-through-endpoints)

---

## Tech stack

| Concern        | Choice                                              |
|----------------|-----------------------------------------------------|
| Language       | Kotlin (JVM 21)                                     |
| Framework      | Spring Boot 4 (Spring Web MVC, Spring Data JPA)     |
| Persistence    | H2 in-memory database (`jdbc:h2:mem:assamockdb`)    |
| API docs       | springdoc OpenAPI / Swagger UI                      |
| Build          | Gradle (`./gradlew`)                                |

---

## Running the mock

```bash
./gradlew bootRun
```

| Property            | Value                          |
|---------------------|--------------------------------|
| Port                | `8067`                         |
| Servlet context path| `/administration`              |
| Base URL            | `http://localhost:8067/administration` |

So every endpoint below is reached at `http://localhost:8067/administration<path>`.

Supporting consoles (no auth required):

- **Swagger UI** — `http://localhost:8067/administration/swagger-ui/index.html`
- **OpenAPI JSON** — `http://localhost:8067/administration/v3/api-docs`
- **H2 console** — `http://localhost:8067/administration/h2-console`
  (JDBC URL `jdbc:h2:mem:assamockdb`, user `sa`, empty password)

Because the database is in-memory, **all data is wiped on every restart**.

---

## Authentication model

The real Keyless API uses an API key plus a per-user access key exchanged for a bearer token.
The mock reproduces this two-step flow.

1. **API key** — every call to `POST /login` must carry the header `X-Api-Key`.
2. **Login** — `POST /login` exchanges a `userId` + `accessKey` for a bearer token.
3. **Bearer token** — every other endpoint requires `Authorization: Bearer <token>`.

The accepted credentials are fixed in `application.yaml` (`mock.auth`):

| Setting       | Value                                    |
|---------------|------------------------------------------|
| `api-key`     | `dev-api-key`                            |
| `user-id`     | `00000000-0000-0000-0000-000000000001`   |
| `access-key`  | `dev-access-key`                         |

Tokens are random UUIDs held in memory (`AuthService`). They **never expire** and are **lost on
restart**. Any number of tokens can be live at once.

Enforcement is done by `AuthInterceptor`, which guards `/**` except these public prefixes:
`/login`, `/swagger-ui`, `/v3/api-docs`, `/h2-console`. A missing or unknown bearer token yields
**401 Unauthorized**.

> Note: the fixed `user-id` used for login is just the API caller's credential. It does **not**
> have to correspond to a `User` record created through `POST /user`; the user store and the login
> credential are independent in the mock.

---

## Conventions

- **JSON** in and out for request/response bodies.
- **IDs** are server-generated UUID strings.
- **List endpoints** return a *page envelope* rather than a bare array:

  ```json
  {
    "items": [ /* ... */ ],
    "nextPageToken": "..."   // omitted when null
  }
  ```

  The mock returns **all** matching records in a single page and never sets `nextPageToken`
  (no real pagination).
- **Null fields are omitted** from responses (`@JsonInclude(NON_NULL)`) on `LockingDevice`,
  `Permission` and the page envelope. So optional/unpopulated fields simply do not appear.
- **Timestamps** in permissions use ISO-8601 `Instant` (e.g. `2026-06-14T10:00:00Z`).
- **Durations** use ISO-8601 period/duration strings (e.g. `P8D` = 8 days).

---

## Endpoints

All paths are relative to the base URL `http://localhost:8067/administration`.

### Login

#### `POST /login`

Exchange credentials for a bearer token.

**Headers**

| Header        | Required | Notes                          |
|---------------|----------|--------------------------------|
| `X-Api-Key`   | yes      | Must equal `dev-api-key`       |
| `Content-Type`| yes      | `application/json`             |

**Request body**

```json
{
  "userId": "00000000-0000-0000-0000-000000000001",
  "accessKey": "dev-access-key"
}
```

**Response `200 OK`**

```json
{ "token": "f1c2…-uuid" }
```

**Errors**

| Status | When                                            |
|--------|-------------------------------------------------|
| `403`  | `X-Api-Key` missing or wrong                    |
| `401`  | `userId` / `accessKey` do not match config      |

---

### Users

Base path `/user`. Represents system users with a `role` of `ADMIN` or `USER`.

#### `GET /user`

List all users.

- **Auth:** bearer
- **Response `200 OK`:**

  ```json
  {
    "items": [
      { "id": "uuid", "role": "USER" }
    ]
  }
  ```

#### `POST /user`

Create a user. The server generates the `id` and `accessKey`.

- **Auth:** bearer
- **Request body:**

  ```json
  { "role": "USER" }
  ```

  `role` must be `ADMIN` or `USER`.
- **Response `201 Created`:**

  ```json
  {
    "id": "uuid",
    "accessKey": "uuid",
    "role": "USER"
  }
  ```

  > The `accessKey` is returned **only** on creation (it is this user's login secret), it is never
  > exposed again by `GET /user` or `GET /user/{userId}`.

#### `GET /user/{userId}`

Fetch a single user.

- **Auth:** bearer
- **Response `200 OK`:** `{ "id": "uuid", "role": "USER" }`
- **`404 Not Found`** if no such user.

#### `DELETE /user/{userId}`

Delete a user.

- **Auth:** bearer
- **Response `204 No Content`** (idempotent — deleting an unknown id still returns `204`).

---

### Locking devices

Base path `/locking-device`. Represents physical locks (padlocks, controllers, key deposits, …).

#### `GET /locking-device`

List all locking devices.

- **Auth:** bearer
- **Response `200 OK`:**

  ```json
  {
    "items": [
      {
        "id": "uuid",
        "name": "Front gate",
        "claimingStatus": "UNCLAIMED"
      }
    ]
  }
  ```

  Hardware/certificate fields are omitted while they are null (see
  [domain model](#locking-device-fields)).

#### `POST /locking-device`

Create a locking device. In the real API this provisions an *unclaimed placeholder* that a
physical device later claims; the mock does the same — it is always created `UNCLAIMED`.

- **Auth:** bearer
- **Request body:**

  ```json
  { "name": "Front gate" }
  ```
- **Response `201 Created`:**

  ```json
  {
    "id": "uuid",
    "name": "Front gate",
    "claimingStatus": "UNCLAIMED"
  }
  ```

#### `GET /locking-device/{lockingDeviceId}`

Fetch a single locking device.

- **Auth:** bearer
- **Response `200 OK`:** the locking device object.
- **`404 Not Found`** if no such device.

#### `DELETE /locking-device/{lockingDeviceId}`

Delete a locking device.

- **Auth:** bearer
- **Response `204 No Content`** (idempotent).

---

### Permissions

Base path `/permission`. A permission links a **user** to a **locking device** for a time window
and authorizes a particular **operation**.

#### `GET /permission`

List permissions, optionally filtered by user.

- **Auth:** bearer
- **Query parameters:**

  | Param      | Required | Notes                                            |
  |------------|----------|--------------------------------------------------|
  | `user-id`  | no       | When present, only that user's permissions are returned |
- **Response `200 OK`:**

  ```json
  {
    "items": [
      {
        "id": "uuid",
        "userId": "uuid",
        "lockingDeviceId": "uuid",
        "operationType": "OPEN",
        "permissionType": "SINGLE_INTERVAL",
        "operatingKeyValidityDuration": "P8D",
        "start": "2026-06-14T10:00:00Z",
        "end": "2026-06-20T10:00:00Z",
        "weekdays": ["MONDAY", "TUESDAY"],
        "intervals": ["08:00-12:00"]
      }
    ]
  }
  ```

  `weekdays` and `intervals` are omitted when empty.

#### `POST /permission`

Grant a permission. Validates that both the user and the locking device exist.

- **Auth:** bearer
- **Request body:**

  ```json
  {
    "userId": "uuid",
    "lockingDeviceId": "uuid",
    "operationType": "OPEN",
    "permissionType": "SINGLE_INTERVAL",
    "start": "2026-06-14T10:00:00Z",
    "end": "2026-06-20T10:00:00Z",
    "operatingKeyValidityDuration": "P8D",
    "weekdays": ["MONDAY", "TUESDAY"],
    "intervals": ["08:00-12:00"]
  }
  ```

  | Field                          | Required | Default | Notes                                                       |
  |--------------------------------|----------|---------|-------------------------------------------------------------|
  | `userId`                       | yes      | —       | Must reference an existing user                             |
  | `lockingDeviceId`              | yes      | —       | Must reference an existing locking device                   |
  | `operationType`                | yes      | —       | `OPEN`, `UPDATE_FIRMWARE`, `UPDATE_TIME`                     |
  | `permissionType`               | yes      | —       | `SINGLE_INTERVAL`, `MULTIPLE_INTERVALS`, `RECURRING`        |
  | `start`                        | yes      | —       | ISO-8601 instant                                            |
  | `end`                          | yes      | —       | ISO-8601 instant                                            |
  | `operatingKeyValidityDuration` | no       | `P8D`   | ISO-8601 duration; lifetime of the operating key            |
  | `weekdays`                     | no       | —       | Set of `MONDAY`…`SUNDAY` (used with `RECURRING`)            |
  | `intervals`                    | no       | —       | Set of interval strings (used with multi/recurring schedules)|
- **Response `201 Created`:**

  ```json
  { "id": "uuid" }
  ```

  > Only the new permission's `id` is returned. Fetch the full object via
  > `GET /permission/{permissionId}`.
- **`404 Not Found`** if the `userId` or `lockingDeviceId` does not exist.

#### `GET /permission/{permissionId}`

Fetch a single permission (full object, same shape as the list items).

- **Auth:** bearer
- **Response `200 OK`** / **`404 Not Found`**.

#### `DELETE /permission/{permissionId}`

Revoke a permission.

- **Auth:** bearer
- **Response `204 No Content`** (idempotent).

---

## Domain model

### User

| Field       | Type   | Notes                                  |
|-------------|--------|----------------------------------------|
| `id`        | string | UUID, server-generated                 |
| `role`      | enum   | `ADMIN`, `USER`                        |
| `accessKey` | string | UUID — returned **only** on creation   |

### Locking device fields

`LockingDevice` carries far more fields than the mock ever populates. The mock only ever sets
`id`, `name` and `claimingStatus`; the remaining fields exist to match the real API's schema and
stay `null` (and therefore omitted from JSON) because nothing in the mock claims a device to real
hardware.

| Field                     | Type   | Populated by mock? | Meaning                                                       |
|---------------------------|--------|--------------------|---------------------------------------------------------------|
| `id`                      | string | yes                | UUID                                                          |
| `name`                    | string | yes                | Human label given at creation                                 |
| `claimingStatus`          | enum   | yes (`UNCLAIMED`)  | `CLAIMED` / `UNCLAIMED`                                       |
| `serialNumber`            | string | no (always null)   | Hardware serial, set when a physical device claims the slot   |
| `hardwareModel`           | enum   | no                 | `PADLOCK`, `KEY_DEPOSIT`, `CONTROLLER`, `SWING_HANDLE`, `SWING_HANDLE_CONNECTED` |
| `hardwareVersion`         | string | no                 | Hardware revision                                             |
| `firmwareVersion`         | string | no                 | Firmware revision                                             |
| `operationalCertificate`  | object | no                 | `{ eligibleForReKeying, expirationDate, revoked }`            |
| `manufacturingCertificate`| object | no                 | `{ eligibleForReKeying, expirationDate, revoked }`            |

### Permission fields

| Field                          | Type            | Notes                                               |
|--------------------------------|-----------------|-----------------------------------------------------|
| `id`                           | string          | UUID                                                |
| `userId`                       | string          | The user granted access                             |
| `lockingDeviceId`              | string          | The target lock                                     |
| `operationType`                | enum            | `OPEN`, `UPDATE_FIRMWARE`, `UPDATE_TIME`            |
| `permissionType`               | enum            | `SINGLE_INTERVAL`, `MULTIPLE_INTERVALS`, `RECURRING`|
| `operatingKeyValidityDuration` | string          | ISO-8601 duration (default `P8D`)                   |
| `start` / `end`                | instant         | Validity window                                     |
| `weekdays`                     | set<DayOfWeek>  | Optional; omitted when empty                        |
| `intervals`                    | set<string>     | Optional; omitted when empty                        |

### Enumerations

- **Role:** `ADMIN`, `USER`
- **ClaimingStatus:** `CLAIMED`, `UNCLAIMED`
- **OperationType:** `OPEN`, `UPDATE_FIRMWARE`, `UPDATE_TIME`
- **PermissionType:** `SINGLE_INTERVAL`, `MULTIPLE_INTERVALS`, `RECURRING`
- **HardwareModel:** `PADLOCK`, `KEY_DEPOSIT`, `CONTROLLER`, `SWING_HANDLE`, `SWING_HANDLE_CONNECTED`
- **ActivationStatus:** `PENDING_ACTIVATION`, `ACTIVATED` *(used by gateway devices — see below)*
- **DeliveryMechanism:** `SMS`, `NONE` *(used by offline keys — see below)*

---

## How the mock differs from the real API

- **Single fixed credential set.** The real API issues per-tenant API keys and per-user access
  keys. The mock accepts exactly one API key / userId / accessKey triple from `application.yaml`.
- **Tokens never expire** and are stored in a plain in-memory set — no JWT, no scopes, no refresh.
- **No real pagination.** List endpoints always return every record in a single page and never
  emit `nextPageToken`, even though the response envelope supports it.
- **Devices are never claimed.** `POST /locking-device` always produces an `UNCLAIMED` placeholder.
  There is no flow to attach real hardware, so `serialNumber`, hardware/firmware versions and
  certificates remain unset.
- **Looser validation.** Beyond the existence checks on `POST /permission` (user + locking device
  must exist), the mock does little semantic validation. Deletes are idempotent and return `204`
  even for unknown ids.
- **No content negotiation header.** The real API requires a versioned `Accept` header
  (`application/vnd.assaabloy.keyless.administration-2+json`); the mock uses plain
  `application/json`.

---

## Data the mock does *not* return through endpoints

Some behaviour and data only exist *behind* the API surface — they are produced by the server, not
supplied by the caller, or they belong to parts of the real API that the mock does not (yet)
expose.

**Server-generated, never accepted as input:**

- **`User.id` and `User.accessKey`** — generated as UUIDs on `POST /user`. The `accessKey` is the
  user's login secret and is shown **once** at creation, never again.
- **`LockingDevice.id`** and **`Permission.id`** — generated UUIDs.
- **Bearer tokens** — random UUIDs minted at login, validated in memory.

**Fields that exist in the schema but the mock leaves empty** (so they are absent from JSON):

- `LockingDevice.serialNumber` — in the real platform this is the **hardware serial number** of the
  physical lock, written when the device is claimed/commissioned. The mock never claims a device,
  so it stays null.
- `LockingDevice.hardwareModel`, `hardwareVersion`, `firmwareVersion`.
- `LockingDevice.operationalCertificate`, `manufacturingCertificate` — the cryptographic
  certificates a real lock carries (with re-keying eligibility, expiry and revocation state).

**Concepts modelled in code but not exposed by any endpoint.** The real Keyless Administration API
includes several more resources. Matching Kotlin models exist in `model/` as a starting point, but
no controllers serve them yet:

| Resource                  | Model class                | Real-API purpose                                                       |
|---------------------------|----------------------------|------------------------------------------------------------------------|
| Operating devices         | `OperatingDevice`          | Mobile apps that operate locks; invited then activated                 |
| Gateway devices           | `GatewayDevice`            | Internet-connected controllers for remote operation; claimed/activated |
| Gateway associations      | `GatewayDeviceAssociation` | Links a gateway to the locking devices it can reach                    |
| Offline keys              | `OfflineKey`               | Time-bounded keys for operating locks without connectivity             |

These are documented here for completeness and to signal intended scope; calling any path for them
returns nothing today.

---

*This mock targets the [ASSA ABLOY Keyless Administration API v2](https://api-docs.keyless.assaabloy.com/administration/v2/index.html)
(modelled against v2.15). Consult the official docs for the authoritative contract.*