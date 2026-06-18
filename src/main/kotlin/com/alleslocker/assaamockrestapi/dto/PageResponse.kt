package com.alleslocker.assaamockrestapi.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PageResponse<T>(
    val items: List<T>,
    val nextPageToken: String? = null
)