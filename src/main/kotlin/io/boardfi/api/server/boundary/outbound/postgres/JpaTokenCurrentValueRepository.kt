package io.boardfi.api.server.boundary.outbound.postgres

import io.boardfi.api.server.domain.token.TokenCurrentValue
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaTokenCurrentValueRepository : CrudRepository<TokenCurrentValue, Long>
