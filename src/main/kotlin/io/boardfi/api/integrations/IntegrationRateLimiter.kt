package io.boardfi.api.integrations

import java.time.Clock
import java.time.Instant

class IntegrationRateLimiter(
	private val limit: Int,
	private val clock: Clock = Clock.systemUTC(),
	internal val executions: MutableList<Instant> = mutableListOf(),
) {
	fun limitReached(): Boolean {
		val (_, secondAgo) = getTimestamps()
		val executionsCount = executions.count { it.isAfter(secondAgo) }
		return executionsCount >= limit
	}

	fun registerExecution() {
		val (now, secondAgo) = getTimestamps()
		executions.addLast(now)
		executions.retainAll { it.isAfter(secondAgo) }
	}

	private fun getTimestamps(): Pair<Instant, Instant> {
		val now = Instant.now(clock)
		val secondAgo = now.minusSeconds(1)
		return Pair(now, secondAgo)
	}
}
