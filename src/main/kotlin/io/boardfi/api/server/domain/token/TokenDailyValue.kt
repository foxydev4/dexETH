package io.boardfi.api.server.domain.token

import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import java.time.Instant

@Entity(name = "token_daily_values")
data class TokenDailyValue(
	@Id
	@GeneratedValue(strategy = IDENTITY)
	val id: Long? = null,

	@ManyToOne(fetch = LAZY)
	val token: Token,

	@Column(nullable = false)
	val price: Double,

	@Column(nullable = false)
	val volume: Double,

	@Column(nullable = false)
	val timestamp: Instant,
)
