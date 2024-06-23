package io.boardfi.api.server.domain.token

import io.boardfi.api.IntegrationTest
import io.boardfi.api.TokenCurrentValueHelpers
import io.boardfi.api.TokenHelpers
import io.boardfi.api.server.infrastructure.BoardfiBadRequestException
import io.boardfi.api.server.infrastructure.BoardfiNotFoundException
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.extensions.clock.TestClock
import io.kotest.matchers.date.shouldBeCloseTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.seconds

class TokenCurrentValueServiceTest(
	private val service: TokenCurrentValueService,
	private val tokenHelpers: TokenHelpers,
	private val tokenCurrentValueHelpers: TokenCurrentValueHelpers,
) : IntegrationTest({
	context("findByTokenId") {
		should("find TokenCurrentValue by token ID") {
			val token = tokenHelpers.defaultToken()
			val id = tokenHelpers.insertToken(token)

			val value = tokenCurrentValueHelpers.defaultTokenCurrentValue(token)
			tokenCurrentValueHelpers.insertTokenCurrentValue(value)

			val result = service.findByTokenId(id)

			result shouldNotBe null
			result?.tokenId?.shouldBeEqual(id)
		}

		should("return null if there is no TokenCurrentValue with given token ID") {
			val result = service.findByTokenId(321357913412)
			result shouldBe null
		}
	}

	context("getByTokenId") {
		should("get TokenCurrentValue by token ID") {
			val token = tokenHelpers.defaultToken()
			val id = tokenHelpers.insertToken(token)

			val value = tokenCurrentValueHelpers.defaultTokenCurrentValue(token)
			tokenCurrentValueHelpers.insertTokenCurrentValue(value)

			val result = service.getByTokenId(id)

			result.tokenId?.shouldBeEqual(id)
		}

		should("throw exception if there is no TokenCurrentValue with given token ID") {
			val searchId = 23421411321

			shouldThrowWithMessage<BoardfiNotFoundException>("TokenCurrentValue not found for token with id=$searchId") {
				service.getByTokenId(searchId)
			}
		}
	}

	context("saveNewCurrentValue") {
		should("save new TokenCurrentValue for token in database") {
			val token = tokenHelpers.defaultToken()
			val id = tokenHelpers.insertToken(token)

			service.saveNewCurrentValue(
				token = token,
				currentPrice = 50.0,
				price1h = 150.0,
				price7d = 234.0,
				price24h = 234.2,
				marketCap = 250.0,
				liquidity = 350.0,
				circulatingSupply = 450.toBigInteger(),
				holders = 550,
				volume = 650,
			)

			val result = service.findByTokenId(id)
			result shouldNotBe null
			result?.tokenId?.shouldBeEqual(id)
			result?.currentPrice?.shouldBeEqual(50.0)
			result?.price1h?.shouldBeEqual(150.0)
			result?.marketCap?.shouldBeEqual(250.0)
			result?.liquidity?.shouldBeEqual(350.0)
			result?.circulatingSupply?.shouldBeEqual(450.toString())
			result?.holders?.shouldBeEqual(550)
			result?.volume?.shouldBeEqual(650)
			result?.timestamp?.shouldBeCloseTo(Instant.now(), 30.seconds)
		}

		should("throw exception if TokenCurrentValue for given token already exists in database") {
			val token = tokenHelpers.defaultToken()
			val id = tokenHelpers.insertToken(token)

			val currentValue = tokenCurrentValueHelpers.defaultTokenCurrentValue(token)
			tokenCurrentValueHelpers.insertTokenCurrentValue(currentValue)

			shouldThrowWithMessage<BoardfiBadRequestException>("TokenCurrentValue for tokenId=$id already exists") {
				service.saveNewCurrentValue(
					token = token,
					currentPrice = 50.0,
					price1h = 150.0,
					price7d = 234.0,
					price24h = 234.2,
					marketCap = 250.0,
					liquidity = 350.0,
					circulatingSupply = 450.toBigInteger(),
					holders = 550,
					volume = 650,
				)
			}
		}
	}

	context("updateExistingCurrentValue") {
		should("update existing TokenCurrentValue for token in database") {
			val clock = TestClock(Instant.parse("2024-02-10T21:37:12.420Z"), ZoneOffset.UTC)
			val token = tokenHelpers.defaultToken()
			val id = tokenHelpers.insertToken(token)

			val beforeUpdate = tokenCurrentValueHelpers.defaultTokenCurrentValue(token, clock)
			tokenCurrentValueHelpers.insertTokenCurrentValue(beforeUpdate)

			clock.plus(15.seconds)

			service.updateExistingCurrentValue(
				token = token,
				currentPrice = 2150.0,
				price1h = 21150.0,
				marketCap = 21250.0,
				liquidity = 21350.0,
				circulatingSupply = 21450.toString(),
				holders = 21550,
				volume = 21650,
				clock = clock,
			)

			val afterUpdate = service.findByTokenId(id)

			afterUpdate shouldNotBe null
			afterUpdate?.tokenId?.shouldBeEqual(id)
			afterUpdate?.currentPrice?.shouldBeEqual(2150.0)
			afterUpdate?.price1h?.shouldBeEqual(21150.0)
			afterUpdate?.marketCap?.shouldBeEqual(21250.0)
			afterUpdate?.liquidity?.shouldBeEqual(21350.0)
			afterUpdate?.circulatingSupply?.shouldBeEqual(21450.toString())
			afterUpdate?.holders?.shouldBeEqual(21550)
			afterUpdate?.volume?.shouldBeEqual(21650)
			afterUpdate?.timestamp?.shouldBeEqual(Instant.parse("2024-02-10T21:37:27.420Z"))
		}

		should("throw exception if TokenCurrentValue for given token does not exist in database") {
			val token = tokenHelpers.defaultToken()
			val id = tokenHelpers.insertToken(token)

			shouldThrowWithMessage<BoardfiNotFoundException>("TokenCurrentValue not found for token with id=$id") {
				service.updateExistingCurrentValue(
					token = token,
					currentPrice = 2150.0,
					price1h = 21150.0,
					marketCap = 21250.0,
					liquidity = 21350.0,
					circulatingSupply = 21450.toString(),
					holders = 21550,
					volume = 21650,
				)
			}
		}
	}

	context("deleteByTokenId") {
		should("delete TokenCurrentValue for given token ID from database") {
			val token = tokenHelpers.defaultToken()
			val id = tokenHelpers.insertToken(token)

			val currentValue = tokenCurrentValueHelpers.defaultTokenCurrentValue(token)
			tokenCurrentValueHelpers.insertTokenCurrentValue(currentValue)

			service.deleteByTokenId(id)

			val result = service.findByTokenId(id)

			result shouldBe null
		}
	}
})
