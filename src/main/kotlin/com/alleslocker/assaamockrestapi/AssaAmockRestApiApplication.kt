package com.alleslocker.assaamockrestapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class AssaAmockRestApiApplication

fun main(args: Array<String>) {
    runApplication<AssaAmockRestApiApplication>(*args)
}