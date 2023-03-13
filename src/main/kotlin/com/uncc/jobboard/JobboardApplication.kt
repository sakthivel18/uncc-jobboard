package com.uncc.jobboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JobboardApplication

fun main(args: Array<String>) {
	runApplication<JobboardApplication>(*args)
}
