package io.boardfi.api.server.domain.token

import io.boardfi.api.IntegrationTest
import io.boardfi.api.TokenDailyValueHelpers
import io.boardfi.api.TokenHelpers
import io.boardfi.api.server.infrastructure.pageable
import io.kotest.matchers.equals.shouldBeEqual
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.DESC
import java.time.Instant

class TokenDailyValueServiceTest(
	private val service: TokenDailyValueService,
	private val tokenHelpers: TokenHelpers,
	private val tokenDailyValueHelpers: TokenDailyValueHelpers,
) : IntegrationTest({
	context("saveDailyValueForToken") {
		should("save TokenDailyValue in database") {
			val token = tokenHelpers.defaultToken()
			val tokenId = tokenHelpers.insertToken(token)

			service.saveDailyValueForToken(
				token = token,
				price = 10.0,
				volume = 20.0,
				timestamp = Instant.parse("2024-02-10T21:37:12.420Z")
			)

			val values = service.findDailyValuesForTokenId(tokenId, pageable(1, 5))

			values.totalElements shouldBeEqual 1
			values.content.size shouldBeEqual 1
			values.content.first().price shouldBeEqual 10.0
			values.content.first().volume shouldBeEqual 20.0
			values.content.first().timestamp shouldBeEqual Instant.parse("2024-02-10T21:37:12.420Z")
		}
	}

	context("findDailyValuesForTokenId") {
		should("find TokenDailyValue page for given tokenId") {
			val token = tokenHelpers.defaultToken()
			val tokenId = tokenHelpers.insertToken(token)

			tokenDailyValueHelpers.insertTokenDailyValue(
				tokenDailyValueHelpers.defaultTokenDailyValue(
					token = token,
					price = 10.0,
					volume = 20.0,
					timestamp = Instant.parse("2024-02-08T00:00:00.000Z")
				)
			)

			tokenDailyValueHelpers.insertTokenDailyValue(
				tokenDailyValueHelpers.defaultTokenDailyValue(
					token = token,
					price = 12.5,
					volume = 22.5,
					timestamp = Instant.parse("2024-02-09T00:00:00.000Z")
				)
			)

			tokenDailyValueHelpers.insertTokenDailyValue(
				tokenDailyValueHelpers.defaultTokenDailyValue(
					token = token,
					price = 15.0,
					volume = 25.0,
					timestamp = Instant.parse("2024-02-10T00:00:00.000Z")
				)
			)

			val pageable1 = pageable(1, 2, Sort.by(DESC, "timestamp"))
			val page1 = service.findDailyValuesForTokenId(tokenId, pageable1)

			page1.totalElements shouldBeEqual 3
			page1.totalPages shouldBeEqual 2
			page1.content.size shouldBeEqual 2

			page1.content.getOrNull(0)?.price?.shouldBeEqual(15.0)
			page1.content.getOrNull(0)?.volume?.shouldBeEqual(25.0)
			page1.content.getOrNull(0)?.timestamp?.shouldBeEqual(Instant.parse("2024-02-10T00:00:00.000Z"))

			page1.content.getOrNull(1)?.price?.shouldBeEqual(12.5)
			page1.content.getOrNull(1)?.volume?.shouldBeEqual(22.5)
			page1.content.getOrNull(1)?.timestamp?.shouldBeEqual(Instant.parse("2024-02-09T00:00:00.000Z"))

			val pageable2 = pageable(2, 2, Sort.by(DESC, "timestamp"))
			val page2 = service.findDailyValuesForTokenId(tokenId, pageable2)

			page2.totalElements shouldBeEqual 3
			page2.totalPages shouldBeEqual 2
			page2.content.size shouldBeEqual 1

			page2.content.getOrNull(0)?.price?.shouldBeEqual(10.0)
			page2.content.getOrNull(0)?.volume?.shouldBeEqual(20.0)
			page2.content.getOrNull(0)?.timestamp?.shouldBeEqual(Instant.parse("2024-02-08T00:00:00.000Z"))
		}

		should("should return empty page if there is no TokenDailyValue for given tokenId") {
			val token = tokenHelpers.defaultToken()
			val tokenId = tokenHelpers.insertToken(token)

			val pageable = pageable(1, 2, Sort.by(DESC, "timestamp"))
			val page = service.findDailyValuesForTokenId(tokenId, pageable)

			page.totalElements shouldBeEqual 0
			page.totalPages shouldBeEqual 0
			page.content.size shouldBeEqual 0
		}
	}

	context("deleteById") {
		should("delete TokenDailyValue by id") {
			val token = tokenHelpers.defaultToken()
			val tokenId = tokenHelpers.insertToken(token)

			val dailyValueId = tokenDailyValueHelpers.insertTokenDailyValue(
				tokenDailyValueHelpers.defaultTokenDailyValue(
					token = token,
					price = 10.0,
					volume = 20.0,
					timestamp = Instant.parse("2024-02-08T00:00:00.000Z")
				)
			)

			service.deleteById(dailyValueId)

			val pageable = pageable(1, 2, Sort.by(DESC, "timestamp"))
			val page = service.findDailyValuesForTokenId(tokenId, pageable)
			page.totalElements shouldBeEqual 0
			page.totalPages shouldBeEqual 0
			page.content.size shouldBeEqual 0
		}
	}

	context("deleteAllByTokenId") {
		should("delete all TokenDailyValues for given tokenId") {
			val token = tokenHelpers.defaultToken()
			val tokenId = tokenHelpers.insertToken(token)

			tokenDailyValueHelpers.insertTokenDailyValue(
				tokenDailyValueHelpers.defaultTokenDailyValue(
					token = token,
					price = 10.0,
					volume = 20.0,
					timestamp = Instant.parse("2024-02-08T00:00:00.000Z")
				)
			)

			tokenDailyValueHelpers.insertTokenDailyValue(
				tokenDailyValueHelpers.defaultTokenDailyValue(
					token = token,
					price = 12.5,
					volume = 22.5,
					timestamp = Instant.parse("2024-02-09T00:00:00.000Z")
				)
			)

			tokenDailyValueHelpers.insertTokenDailyValue(
				tokenDailyValueHelpers.defaultTokenDailyValue(
					token = token,
					price = 15.0,
					volume = 25.0,
					timestamp = Instant.parse("2024-02-10T00:00:00.000Z")
				)
			)

			val pageBeforeDelete = service.findDailyValuesForTokenId(tokenId, pageable(1, 5))
			pageBeforeDelete.totalElements shouldBeEqual 3
			pageBeforeDelete.content.size shouldBeEqual 3

			service.deleteAllByTokenId(tokenId)

			val pageAfterDelete = service.findDailyValuesForTokenId(tokenId, pageable(1, 5))
			pageAfterDelete.totalElements shouldBeEqual 0
			pageAfterDelete.content.size shouldBeEqual 0
		}
	}
})
