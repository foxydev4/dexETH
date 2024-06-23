package io.boardfi.api.server.boundary.inbound.document

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
import java.math.BigDecimal
import java.math.BigInteger

@Schema(name = "TokenDetailsDocument", description = "Token with additional data like social media links")
data class TokenDetailsDocument(
	@field:Schema(description = "ID of the Token", requiredMode = REQUIRED)
	val id: Long?,

	@field:Schema(description = "Name of the Token", requiredMode = REQUIRED)
	val name: String,

	@field:Schema(description = "Current Price of the Token", requiredMode = NOT_REQUIRED)
	val price: BigDecimal?,

	@field:Schema(description = "Price change percentage from last 24 hours", requiredMode = NOT_REQUIRED)
	val priceChange24h: Double?,

	@field:Schema(description = "Volume from last 24 hours", requiredMode = NOT_REQUIRED)
	val volume24h: BigInteger?,

	@field:Schema(description = "Current Market Cap", requiredMode = NOT_REQUIRED)
	val marketCap: BigDecimal?,

	@field:Schema(description = "Total Supply of the Token", requiredMode = REQUIRED)
	val totalSupply: String,

	@field:Schema(description = "Boardfi score", requiredMode = REQUIRED)
	val score: Int,

	@field:Schema(description = "True if Token owner paid for premium listing", requiredMode = REQUIRED)
	val premium: Boolean,

	@field:Schema(description = "Social media links", requiredMode = REQUIRED)
	val socialMedia: SocialMediaDocument,
)

@Schema(name = "SocialMediaDocument", description = "Link to website and social media pages")
data class SocialMediaDocument(
	@field:Schema(requiredMode = NOT_REQUIRED)
	val website: String?,

	@field:Schema(requiredMode = NOT_REQUIRED)
	val discord: String?,

	@field:Schema(requiredMode = NOT_REQUIRED)
	val telegram: String?,

	@field:Schema(requiredMode = NOT_REQUIRED)
	val twitter: String?,
)
