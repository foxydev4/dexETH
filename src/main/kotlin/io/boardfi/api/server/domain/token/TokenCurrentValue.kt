package io.boardfi.api.server.domain.token

import jakarta.persistence.*
import java.time.Instant

@Entity(name = "token_current_values")
data class TokenCurrentValue(
	@Id
	val tokenId: Long? = null,

	@OneToOne(optional = false)
	@MapsId
	val token: Token,

	@Column(nullable = false, name = "current_price")
	val currentPrice: Double,

	@Column(nullable = false, name = "price_1h")
	val price1h: Double,

	@Column(nullable = false, name = "price_24h")
	val price24h: Double,

	@Column(nullable = false, name = "price_7d")
	val price7d: Double,

	@Column(nullable = false, name = "market_cap")
	val marketCap: Double,

	@Column(nullable = false)
	val liquidity: Double,

	@Column(nullable = false, name = "circulating_supply")
	val circulatingSupply: String,

	@Column(nullable = false)
	val holders: Long,

	@Column(nullable = false)
	val volume: Long,

	@Column(nullable = false)
	val timestamp: Instant,
)
