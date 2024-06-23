package io.boardfi.api.server.domain.token

import io.boardfi.api.IntegrationTest
import io.boardfi.api.TokenHelpers
import io.boardfi.api.server.infrastructure.BoardfiBadRequestException
import io.boardfi.api.server.infrastructure.BoardfiNotFoundException
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.date.shouldBeCloseTo
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class TokenServiceTest(
	private val service: TokenService,
	private val tokenHelpers: TokenHelpers,
) : IntegrationTest({
	context("save") {
		should("insert Token entity in the database") {
			val token = tokenHelpers.defaultToken()

			val id = service.save(token)
			val result = service.findById(id)

			result shouldNotBe null
			result?.id?.shouldBeEqual(id)
			result?.createdAt?.shouldBeCloseTo(Instant.now(), 30.seconds)
			result?.shouldBeEqualToIgnoringFields(token, Token::id, Token::createdAt)
		}

		should("throw exception if token with given address is already in the database") {
			val token1 = tokenHelpers.defaultToken(address = "address-1")
			val token2 = tokenHelpers.defaultToken(address = "address-1")

			service.save(token1)

			shouldThrowWithMessage<BoardfiBadRequestException>("token with address=address-1 already exists") {
				service.save(token2)
			}
		}
	}

	context("findById") {
		should("find Token entity by ID") {
			val token = tokenHelpers.defaultToken()
			val id = service.save(token)

			val result = service.findById(id)

			result shouldNotBe null
			result?.id?.shouldBeEqual(id)
			result?.shouldBeEqualToIgnoringFields(token, Token::id, Token::createdAt)
		}

		should("return null when Token with given ID does not exist") {
			val token = tokenHelpers.defaultToken()
			service.save(token)

			val result = service.findById(13124719231)

			result shouldBe null
		}
	}

	context("getById") {
		should("get Token entity by ID") {
			val token = tokenHelpers.defaultToken()
			val id = service.save(token)

			val result = service.getById(id)

			result.id?.shouldBeEqual(id)
			result.shouldBeEqualToIgnoringFields(token, Token::id, Token::createdAt)
		}

		should("throw BoardfiNotFoundException when Token with given ID does not exist") {
			val searchId = 13124719231

			shouldThrowWithMessage<BoardfiNotFoundException>("token not found") {
				service.getById(searchId)
			}
		}
	}

	context("findByAddress") {
		should("find Token by Address") {
			val token = tokenHelpers.defaultToken(address = "address-1")
			service.save(token)

			val result = service.findByAddress("address-1")

			result shouldNotBe null
			result?.address?.shouldBeEqual("address-1")
			result?.shouldBeEqualToIgnoringFields(token, Token::id, Token::createdAt)
		}

		should("return null when Token with given Address does not exist") {
			val token = tokenHelpers.defaultToken(address = "address-1")
			service.save(token)

			val result = service.findByAddress("some-other-address")

			result shouldBe null
		}
	}

	context("getByAddress") {
		should("get Token by Address") {
			val token = tokenHelpers.defaultToken(address = "address-1")
			service.save(token)

			val result = service.getByAddress("address-1")

			result.address shouldBeEqual "address-1"
			result.shouldBeEqualToIgnoringFields(token, Token::id, Token::createdAt)
		}

		should("throw BoardfiNotFoundException when Token with given Address does not exist") {
			val searchAddress = "some-address"

			shouldThrowWithMessage<BoardfiNotFoundException>("token not found") {
				service.getByAddress(searchAddress)
			}
		}
	}

	context("deleteById") {
		should("delete Token with given ID from database") {
			val token = tokenHelpers.defaultToken()
			val id = service.save(token)

			val beforeDelete = service.findById(id)
			beforeDelete shouldNotBe null
			beforeDelete?.id?.shouldBeEqual(id)

			service.deleteById(id)

			val afterDelete = service.findById(id)
			afterDelete shouldBe null
		}
	}

	context("deleteByAddress") {
		should("delete Token with given Address from database") {
			val address = "address-to-delete"
			val token = tokenHelpers.defaultToken(address = address)
			service.save(token)

			val beforeDelete = service.findByAddress(address)
			beforeDelete shouldNotBe null
			beforeDelete?.address?.shouldBeEqual(address)

			service.deleteByAddress(address)

			val afterDelete = service.findByAddress(address)
			afterDelete shouldBe null
		}
	}
})
