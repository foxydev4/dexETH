package io.boardfi.api.server.domain.token

import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenDailyValueRepository
import io.boardfi.api.server.infrastructure.BoardfiInternalException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TokenDailyValueService(private val repository: JpaTokenDailyValueRepository) {

	fun saveDailyValueForToken(token: Token, price: Double, volume: Double, timestamp: Instant): Long {
		val dailyValue = TokenDailyValue(
			token = token,
			price = price,
			volume = volume,
			timestamp = timestamp,
		)

		return repository.save(dailyValue).id ?: throw BoardfiInternalException("missing id for TokenDailyValue")
	}

	fun findDailyValuesForTokenId(tokenId: Long, pageable: Pageable): Page<TokenDailyValue> {
		return repository.findAllByTokenId(tokenId, pageable)
	}

	fun deleteById(id: Long) {
		repository.deleteById(id)
	}

	fun deleteAllByTokenId(tokenId: Long) {
		repository.deleteAllByTokenId(tokenId)
	}

}
