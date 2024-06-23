package io.boardfi.api.integrations.thegraph

import com.apollographql.apollo3.ApolloClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApolloClientConfig(private val properties: ApolloClientProperties) {

	@Bean
	fun uniswapV3Client() = ApolloClient.Builder().serverUrl(properties.serverUrlV3).build()

	@Bean
	fun uniswapV2Client() = ApolloClient.Builder().serverUrl(properties.serverUrlV2).build()
	
}
