package com.simhyeonmin.assignment

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

import org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration

@EnableJpaAuditing
@SpringBootApplication(exclude = [ContextFunctionCatalogAutoConfiguration::class])
class BackendAssignmentApplication

fun main(args: Array<String>) {
	dotenv {
		ignoreIfMissing = true
	}
	runApplication<BackendAssignmentApplication>(*args)
}
