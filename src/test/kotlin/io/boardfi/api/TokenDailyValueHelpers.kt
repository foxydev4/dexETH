package io.boardfi.api

import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenDailyValueRepository
import io.boardfi.api.server.domain.token.Token
import io.boardfi.api.server.domain.token.TokenDailyValue
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@Component
@ActiveProfiles("integration-tests")
class TokenDailyValueHelpers(private val repository: JpaTokenDailyValueRepository) {

	fun insertTokenDailyValue(tokenDailyValue: TokenDailyValue): Long {
		return repository.save(tokenDailyValue).id!!
	}

	fun defaultTokenDailyValue(
		token: Token,
		price: Double = 50.0,
		volume: Double = 100.0,
		timestamp: Instant = Instant.parse("2024-02-10T12:45:12.543Z"),
	): TokenDailyValue {
		return TokenDailyValue(
			token = token,
			price = price,
			volume = volume,
			timestamp = timestamp,
		)
	}

}
