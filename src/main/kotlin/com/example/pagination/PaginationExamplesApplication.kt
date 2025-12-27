package com.example.pagination

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode

@SpringBootApplication
class PaginationExamplesApplication

fun main(args: Array<String>) {
	runApplication<PaginationExamplesApplication>(*args)
}
