package com.simhyeonmin.assignment

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackendAssignmentApplication

fun main(args: Array<String>) {
	dotenv {
		ignoreIfMissing = true
	}
	runApplication<BackendAssignmentApplication>(*args)
}
