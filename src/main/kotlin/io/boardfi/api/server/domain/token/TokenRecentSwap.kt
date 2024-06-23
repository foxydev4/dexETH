package io.boardfi.api.server.domain.token

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY
import java.time.Instant

@Entity(name = "token_recent_swaps")
data class TokenRecentSwap(
	@Id
	@GeneratedValue(strategy = IDENTITY)
	val id: Long? = null,

	@ManyToOne(fetch = FetchType.LAZY)
	val token: Token,

	@Column(nullable = false)
	val price: Double,

	@Column(nullable = false)
	val volume: Double,

	@Column(nullable = false)
	val timestamp: Instant,
)
