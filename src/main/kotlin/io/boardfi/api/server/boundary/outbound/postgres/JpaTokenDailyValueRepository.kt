package io.boardfi.api.server.boundary.outbound.postgres

import io.boardfi.api.server.domain.token.TokenDailyValue
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaTokenDailyValueRepository : CrudRepository<TokenDailyValue, Long> {
	fun findAllByTokenId(tokenId: Long, pageable: Pageable): Page<TokenDailyValue>
	fun deleteAllByTokenId(tokenId: Long)
}
