package io.boardfi.api

import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenCurrentValueRepository
import io.boardfi.api.server.domain.token.Token
import io.boardfi.api.server.domain.token.TokenCurrentValue
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.Instant

@Component
@ActiveProfiles("integration-tests")
class TokenCurrentValueHelpers(private val repository: JpaTokenCurrentValueRepository) {
	fun insertTokenCurrentValue(tokenCurrentValue: TokenCurrentValue): Long {
		return repository.save(tokenCurrentValue).tokenId!!
	}

	fun defaultTokenCurrentValue(token: Token, clock: Clock = Clock.systemUTC()): TokenCurrentValue {
		return TokenCurrentValue(
			token = token,
			currentPrice = 500.0,
			price1h = 1000.0,
			price7d = 234234.2,
			price24h = 234.2,
			marketCap = 1500.0,
			liquidity = 2000.0,
			circulatingSupply = 2500.toString(),
			holders = 3000,
			volume = 3500,
			timestamp = Instant.now(clock),
		)
	}
}
