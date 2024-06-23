package io.boardfi.api

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.spring.SpringExtension
import jakarta.transaction.Transactional
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("integration-tests")
@Testcontainers
@Transactional
@Rollback
abstract class IntegrationTest(body: ShouldSpec.() -> Unit = {}) : ShouldSpec(body) {
	override fun extensions() = listOf(SpringExtension)

	companion object {
		@Container
		@JvmField
		var container = PostgreSQLContainer<Nothing>("postgres:16")

		init {
			container.start()
		}

		@DynamicPropertySource
		@JvmStatic
		fun postgresProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.datasource.url") { container.jdbcUrl }
			registry.add("spring.datasource.username") { container.username }
			registry.add("spring.datasource.password") { container.password }
		}
	}
}
