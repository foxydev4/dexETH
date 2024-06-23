package io.boardfi.api.integrations.thegraph

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "integrations.thegraph")
data class ApolloClientProperties(
	val serverUrlV3: String,
	val serverUrlV2: String,
)
