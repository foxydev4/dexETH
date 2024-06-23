package io.boardfi.api.integrations.thegraph

import com.apollographql.apollo3.ApolloClient
import io.boardfi.api.integrations.v2.FetchSwapsV2Query
import io.boardfi.api.integrations.v2.FetchTokenDailyV2Query
import io.boardfi.api.integrations.v3.FetchSwapsV3Query
import io.boardfi.api.integrations.v3.FetchTokenDailyV3Query
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class TheGraphClient(
	private val uniswapV3Client: ApolloClient,
	private val uniswapV2Client: ApolloClient,
) {

	fun fetchSwapsV3(from: Long, till: Long) = runBlocking {
		val response = uniswapV3Client.query(FetchSwapsV3Query(from, till)).execute()
		return@runBlocking response.data?.swaps ?: emptyList()
	}

	fun fetchSwapsV2(from: Long, till: Long) = runBlocking {
		val response = uniswapV2Client.query(FetchSwapsV2Query(from, till)).execute()
		return@runBlocking response.data?.swaps ?: emptyList()
	}

	fun fetchTokenDailyDataV3(tokenAddress: String) = runBlocking {
		val response = uniswapV3Client.query(FetchTokenDailyV3Query(tokenAddress)).execute()
		return@runBlocking response.data?.tokenDayDatas ?: emptyList()
	}

	fun fetchTokenDailyDataV2(tokenAddress: String) = runBlocking {
		val response = uniswapV2Client.query(FetchTokenDailyV2Query(tokenAddress)).execute()
		return@runBlocking response.data?.tokenDayDatas ?: emptyList()
	}

}
