package io.boardfi.api.server.infrastructure

import io.boardfi.api.integrations.alchemy.AlchemyProperties
import io.boardfi.api.integrations.thegraph.ApolloClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
	ApolloClientProperties::class,
	AlchemyProperties::class,
)
class PropertiesConfig
