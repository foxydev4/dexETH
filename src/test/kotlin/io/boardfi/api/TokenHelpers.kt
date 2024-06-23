package io.boardfi.api

import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenRepository
import io.boardfi.api.server.domain.token.PoolType
import io.boardfi.api.server.domain.token.Token
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import java.time.Instant

@Component
@ActiveProfiles("integration-tests")
class TokenHelpers(private val tokenRepository: JpaTokenRepository) {
	fun insertToken(token: Token): Long {
		return tokenRepository.save(token).id!!
	}

	fun defaultToken(suffix: String = "", address: String = "test-address") = Token(
		name = "test-name$suffix",
		poolId = "test-pool-id$suffix",
		poolType = PoolType.V3,
		address = "$address$suffix",
		ticker = "test-ticker$suffix",
		chain = "test-chain$suffix",
		creationTimestamp = Instant.parse("2024-01-05T12:15:03.135Z"),
		score = 95,
		paidListing = false,
		website = "test-website$suffix",
		discord = "test-discord$suffix",
		telegram = "test-telegram$suffix",
		decimals = 10,
		twitter = "test-twitter$suffix",
		poolToken0 = "test-pool-token0$suffix",
		poolToken1 = "test-pool-token1$suffix",
		logo = "test",
		supply = 100_000_000.toString(),
	)
}
