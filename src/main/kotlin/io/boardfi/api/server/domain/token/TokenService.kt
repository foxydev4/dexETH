package io.boardfi.api.server.domain.token

import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenRepository
import io.boardfi.api.server.infrastructure.BoardfiBadRequestException
import io.boardfi.api.server.infrastructure.BoardfiInternalException
import io.boardfi.api.server.infrastructure.BoardfiNotFoundException
import io.boardfi.api.server.infrastructure.getLogger
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TokenService(private val tokenRepository: JpaTokenRepository) {

	fun save(token: Token): Long {
		if (tokenRepository.existsByAddress(token.address)) {
			throw BoardfiBadRequestException("token with address=${token.address} already exists")
		}

		return tokenRepository
			.save(token)
			.also { logger.info("Token with address=${it.address} saved successfully") }
			.id ?: throw BoardfiInternalException("ERROR: missing id after insert")
	}

	fun findById(id: Long): Token? {
		return tokenRepository.findByIdOrNull(id)
	}

	@Throws(BoardfiNotFoundException::class)
	fun getById(id: Long): Token {
		return findById(id) ?: throw BoardfiNotFoundException("token not found")
	}

	fun findByAddress(address: String): Token? {
		return tokenRepository.findByAddress(address)
	}

	@Throws(BoardfiNotFoundException::class)
	fun getByAddress(address: String): Token {
		return findByAddress(address) ?: throw BoardfiNotFoundException("token not found")
	}

	fun findByPoolId(poolId: String): Token? {
		return tokenRepository.findByPoolId(poolId)
	}

	@Throws(BoardfiNotFoundException::class)
	fun getByPoolId(poolId: String): Token {
		return findByPoolId(poolId) ?: throw BoardfiNotFoundException("token not found")
	}

	fun deleteById(id: Long) {
		tokenRepository.deleteById(id)
		logger.info("Token with id=$id deleted successfully")
	}

	fun deleteByAddress(address: String) {
		tokenRepository.deleteByAddress(address)
		logger.info("Token with address=$address deleted successfully")
	}

	fun getTokens(pageable: Pageable): Page<Token> {
		return tokenRepository.findAll(pageable)
	}


	fun findAllByMarketCapASC(pageable: Pageable):  Page<Token> {
		return tokenRepository.findByOrderByCurrentValue_MarketCapAsc(pageable)
	}
	fun findAllByMarketCapDESC(pageable: Pageable):  Page<Token> {
		return tokenRepository.findByOrderByCurrentValue_MarketCapDesc(pageable)
	}

	fun findAllByVolumeASC(pageable: Pageable):  Page<Token> {
		return tokenRepository.findByOrderByCurrentValue_VolumeAsc(pageable)
	}

	fun findAllByVolumeDESC(pageable: Pageable):  Page<Token> {
		return tokenRepository.findByOrderByCurrentValue_VolumeDesc(pageable)
	}

	fun findAllByPriceASC(pageable: Pageable):  Page<Token> {
		return tokenRepository.findByOrderByCurrentValue_CurrentPriceAsc(pageable)
	}

	fun findAllByPriceDESC(pageable: Pageable):  Page<Token> {
		return tokenRepository.findByOrderByCurrentValue_CurrentPriceDesc(pageable)
	}

	companion object {
		private val logger = getLogger()
	}
}
