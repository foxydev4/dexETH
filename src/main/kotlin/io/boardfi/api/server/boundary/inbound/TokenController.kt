package io.boardfi.api.server.boundary.inbound

import io.boardfi.api.server.boundary.inbound.document.SocialMediaDocument
import io.boardfi.api.server.boundary.inbound.document.TokenDetailsDocument
import io.boardfi.api.server.boundary.inbound.document.TokenDocument
import io.boardfi.api.server.boundary.inbound.document.TokenDocumentPage
import io.boardfi.api.server.domain.token.Token
import io.boardfi.api.server.domain.token.TokenService
import io.boardfi.api.server.infrastructure.pageable
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Tag(name = "Tokens")
@Validated
@RestController
@RequestMapping("/api/tokens")
class TokenController(private val tokenService: TokenService) {

	@Operation(summary = "Search for Tokens")
	@ApiResponse(responseCode = "200", useReturnTypeSchema = true)
	@GetMapping
	fun handleFetchTokens(
		@Parameter(description = "Page number starting from 1", schema = Schema(type = "string", example = "1"))
		@RequestParam(defaultValue = "1") page: Int = 1,
		@Parameter(description = "Page size - min 1, max 1000", schema = Schema(type = "string", example = "25"))
		@RequestParam(defaultValue = "25") size: Int = 25,
		@Parameter(description = "Order by field - marketcap, volume, price", schema = Schema(type = "string", example = "marketcap"))
		@RequestParam(defaultValue = "marketCap") orderBy: String = "marketcap",
		@Parameter(description = "Sort direction - asc, desc", schema = Schema(type = "string", example = "asc"))
		@RequestParam(defaultValue = "asc") direction: String = "asc"
	): TokenDocumentPage {
		val tokensPage = when (orderBy.lowercase(Locale.getDefault())) {
			"marketcap" -> if (direction.lowercase(Locale.getDefault()) == "asc") tokenService.findAllByMarketCapASC(pageable(page, size)) else tokenService.findAllByMarketCapDESC(pageable(page, size))
			"volume" -> if (direction.lowercase(Locale.getDefault()) == "asc") tokenService.findAllByVolumeASC(pageable(page, size)) else tokenService.findAllByVolumeDESC(pageable(page, size))
			"price" -> if (direction.lowercase(Locale.getDefault()) == "asc") tokenService.findAllByPriceASC(pageable(page, size)) else tokenService.findAllByPriceDESC(pageable(page, size))
			else -> throw IllegalArgumentException("Invalid orderBy value: $orderBy")
		}
		return TokenDocumentPage(tokensPage.totalElements, tokensPage.content.map { token -> token.toDocument() })
	}

	@Operation(summary = "Fetch Token by ID")
	@ApiResponses(
		value = [
			ApiResponse(responseCode = "200", useReturnTypeSchema = true),
			ApiResponse(responseCode = "404", description = "Token with given ID not found"),
		]
	)
	@GetMapping("/{id}")
	@ExceptionHandler(ResponseStatusException::class)
	fun handleFetchTokenById(
		@Parameter(description = "ID of the Token", schema = Schema(type = "integer", example = "1"))
		@PathVariable(required = true) id: Long
	): TokenDetailsDocument {
		return tokenService.findById(id)?.toDetailsDocument()
			?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "token not found")
	}

	private fun Token.toDocument(): TokenDocument {
		return TokenDocument(
			id = this.id,
			name = this.name,
			price = this.currentValue?.currentPrice?.toBigDecimal(),
			priceChange1h = this.currentValue?.price1h,
			priceChange24h = null, // TODO:
			priceChange7d = null, // TODO:
			volume24h = this.currentValue?.volume?.toBigInteger(),
			marketCap = this.currentValue?.marketCap?.toBigDecimal(),
			score = this.score,
			premium = this.paidListing,
		)
	}

	private fun Token.toDetailsDocument(): TokenDetailsDocument {
		return TokenDetailsDocument(
			id = this.id,
			name = this.name,
			price = this.currentValue?.currentPrice?.toBigDecimal(),
			priceChange24h = this.currentValue?.price1h,
			volume24h = this.currentValue?.volume?.toBigInteger(),
			marketCap = this.currentValue?.marketCap?.toBigDecimal(),
			totalSupply = this.supply,
			score = this.score,
			premium = this.paidListing,
			socialMedia = SocialMediaDocument(
				website = this.website,
				discord = this.discord,
				telegram = this.telegram,
				twitter = this.twitter,
			),
		)
	}

}
