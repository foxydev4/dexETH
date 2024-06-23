package io.boardfi.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BoardfiApiApplication

fun main(args: Array<String>) {
	runApplication<BoardfiApiApplication>(*args)
}
