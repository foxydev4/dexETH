package io.boardfi.api.integrations.alchemy

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "integrations.alchemy")
data class AlchemyProperties(val apiKey: String)