package com.alleslocker.assaamockrestapi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "mock.auth")
data class MockAuthProperties(
    val apiKey: String,
    val userId: String,
    val accessKey: String
)