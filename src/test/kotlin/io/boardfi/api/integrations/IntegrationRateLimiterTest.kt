package io.boardfi.api.integrations

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.clock.TestClock
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.equals.shouldBeEqual
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.milliseconds

class IntegrationRateLimiterTest : ShouldSpec({
	context("limitReached") {
		should("return false if there is less than 5 executions registered") {
			val limiter = IntegrationRateLimiter(5)
			repeat(4) { limiter.registerExecution() }

			limiter.limitReached() shouldBeEqual false
		}

		should("return true if there are 5 or more executions registered") {
			val limiter = IntegrationRateLimiter(5)
			repeat(5) { limiter.registerExecution() }

			limiter.limitReached() shouldBeEqual true
		}
	}

	context("registerExecution") {
		val clock = TestClock(Instant.parse("2024-01-26T12:30:00.000Z"), ZoneOffset.UTC)
		val limiter = IntegrationRateLimiter(5, clock)

		should("store all executions from last second") {
			repeat(4) {
				limiter.registerExecution()
				clock.plus(300.milliseconds)
			}

			limiter.executions.size shouldBeEqual 4
			limiter.executions shouldContainOnly listOf(
				Instant.parse("2024-01-26T12:30:00.000Z"),
				Instant.parse("2024-01-26T12:30:00.300Z"),
				Instant.parse("2024-01-26T12:30:00.600Z"),
				Instant.parse("2024-01-26T12:30:00.900Z"),
			)
		}

		should("remove executions older than 1 second from cache") {
			limiter.registerExecution()

			limiter.executions.size shouldBeEqual 4
			limiter.executions shouldContainOnly listOf(
				Instant.parse("2024-01-26T12:30:00.300Z"),
				Instant.parse("2024-01-26T12:30:00.600Z"),
				Instant.parse("2024-01-26T12:30:00.900Z"),
				Instant.parse("2024-01-26T12:30:01.200Z"),
			)
		}
	}
})
