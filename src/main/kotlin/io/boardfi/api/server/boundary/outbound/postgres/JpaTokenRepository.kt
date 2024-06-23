package io.boardfi.api.server.boundary.outbound.postgres

import io.boardfi.api.server.domain.token.Token
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaTokenRepository : CrudRepository<Token, Long> {

	fun findByOrderByCurrentValue_MarketCapAsc(pageable: Pageable): Page<Token>
	fun findByOrderByCurrentValue_MarketCapDesc(pageable: Pageable): Page<Token>
	fun findByOrderByCurrentValue_VolumeAsc(pageable: Pageable): Page<Token>
	fun findByOrderByCurrentValue_VolumeDesc(pageable: Pageable): Page<Token>
	fun findByOrderByCurrentValue_CurrentPriceAsc(pageable: Pageable): Page<Token>
	fun findByOrderByCurrentValue_CurrentPriceDesc(pageable: Pageable): Page<Token>
	fun findByPoolId(poolId: String): Token?
	fun findByAddress(address: String): Token?
	fun existsByAddress(address: String): Boolean
	fun deleteByAddress(address: String)
	fun findAll(pageable: Pageable): Page<Token>
}
