package io.boardfi.api.server.domain.token

import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenCurrentValueRepository
import io.boardfi.api.server.infrastructure.BoardfiBadRequestException
import io.boardfi.api.server.infrastructure.BoardfiInternalException
import io.boardfi.api.server.infrastructure.BoardfiNotFoundException
import io.boardfi.api.server.infrastructure.getLogger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.Clock
import java.time.Instant

@Service
class TokenCurrentValueService(private val repository: JpaTokenCurrentValueRepository) {

	fun findByTokenId(id: Long): TokenCurrentValue? {
		return repository.findByIdOrNull(id)
	}

	fun getByTokenId(id: Long): TokenCurrentValue {
		return findByTokenId(id) ?: throw BoardfiNotFoundException("TokenCurrentValue not found for token with id=$id")
	}

	fun saveNewCurrentValue(
		token: Token,
		currentPrice: Double,
		price1h: Double,
		price24h: Double,
		price7d: Double,
		marketCap: Double,
		liquidity: Double,
		circulatingSupply: BigInteger,
		holders: Long,
		volume: Long,
		clock: Clock = Clock.systemUTC(),
	) {
		val id = token.id ?: throw BoardfiInternalException("missing token id")
		if (repository.existsById(id)) {
			throw BoardfiBadRequestException("TokenCurrentValue for tokenId=$id already exists")
		}

		val currentValue = TokenCurrentValue(
			token = token,
			currentPrice = currentPrice,
			price1h = price1h,
			price7d = price7d,
			price24h = price24h,
			marketCap = marketCap,
			liquidity = liquidity,
			circulatingSupply = circulatingSupply.toString(),
			holders = holders,
			volume = volume,
			timestamp = Instant.now(clock),
		)

		repository.save(currentValue)
	}

	fun updateExistingCurrentValue(
		token: Token,
		currentPrice: Double?,
		price1h: Double?,
		marketCap: Double?,
		liquidity: Double?,
		circulatingSupply: String?,
		holders: Long?,
		volume: Long?,
		clock: Clock = Clock.systemUTC(),
	) {
		val id = token.id ?: throw BoardfiInternalException("missing token id")
		val existingCurrentValue = getByTokenId(id)
		val updatedCurrentValue = existingCurrentValue.copy(
			currentPrice = currentPrice ?: existingCurrentValue.currentPrice,
			price1h = price1h ?: existingCurrentValue.price1h,
			marketCap = marketCap ?: existingCurrentValue.marketCap,
			liquidity = liquidity ?: existingCurrentValue.liquidity,
			circulatingSupply = circulatingSupply ?: existingCurrentValue.circulatingSupply,
			holders = holders ?: existingCurrentValue.holders,
			volume = volume ?: existingCurrentValue.volume,
			timestamp = Instant.now(clock)
		)

		repository.save(updatedCurrentValue)
	}

	fun deleteByTokenId(id: Long) {
		repository.deleteById(id)
		logger.info("TokenCurrentValue for tokenId=$id deleted successfully")
	}

	companion object {
		private val logger = getLogger()
	}

}
