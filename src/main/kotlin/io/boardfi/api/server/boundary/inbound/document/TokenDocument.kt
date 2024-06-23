package io.boardfi.api.server.boundary.inbound.document

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import java.math.BigDecimal
import java.math.BigInteger

@Schema(name = "TokenDocument", description = "Token with basic data")
data class TokenDocument(
	@field:Schema(description = "ID of the Token", requiredMode = REQUIRED)
	val id: Long?,

	@field:Schema(description = "Name of the Token", requiredMode = REQUIRED)
	val name: String,

	@field:Schema(description = "Current Price of the Token", requiredMode = NOT_REQUIRED)
	val price: BigDecimal?,

	@field:Schema(description = "Price change percentage from last 1 hour", requiredMode = NOT_REQUIRED)
	val priceChange1h: Double?,

	@field:Schema(description = "Price change percentage from last 24 hours", requiredMode = NOT_REQUIRED)
	val priceChange24h: Double?,

	@field:Schema(description = "Price change percentage from last 7 days", requiredMode = NOT_REQUIRED)
	val priceChange7d: Double?,

	@field:Schema(description = "Volume from last 24 hours", requiredMode = NOT_REQUIRED)
	val volume24h: BigInteger?,

	@field:Schema(description = "Current Market Cap", requiredMode = NOT_REQUIRED)
	val marketCap: BigDecimal?,

	@field:Schema(description = "Boardfi score", requiredMode = REQUIRED)
	val score: Int,

	@field:Schema(description = "True if Token owner paid for premium listing", requiredMode = REQUIRED)
	val premium: Boolean,
)
