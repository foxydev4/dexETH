package io.boardfi.api.integrations.alchemy

import io.boardfi.api.integrations.alchemy.model.AlchemyMetadataPayload
import io.boardfi.api.integrations.alchemy.model.AlchemyMetadataResponse
import io.boardfi.api.server.infrastructure.BoardfiException
import io.boardfi.api.server.infrastructure.getLogger
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class AlchemyRestService(val alchemyProperties: AlchemyProperties) : AlchemyService {
    override fun fetchMetadata(address: String): AlchemyMetadataResponse {
        return callAlchemyApi(AlchemyAction.FETCH_METADATA) {
            val alchemyMetadataBodyRequestModel = AlchemyMetadataPayload(
                id = 1,
                jsonrpc = "2.0",
                method = "alchemy_getTokenMetadata",
                params = listOf(address)
            )
            val response = baseRestClient()
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(alchemyMetadataBodyRequestModel).retrieve().body<AlchemyMetadataResponse>()

            if (response == null) {
                log.error("Failed to get data from Alchemy API")
                throw BoardfiException("Unexpected error from Alchemy API")
            }
            return@callAlchemyApi response;
        }
    }


    private fun <T> callAlchemyApi(action: AlchemyAction, callback: () -> T): T {
        try {
            return callback()
        } catch (e: HttpStatusCodeException) {
            log.error("Failed to fetch latest data from Alchemy API. Reason: ${e.message}", e)
            throw BoardfiException("Unexpected error from Alchemy API")
        }
    }

    private fun baseRestClient() = RestClient.builder()
        .baseUrl(BASE_URL + "/" + alchemyProperties.apiKey)
        .build()

    companion object {
        private const val BASE_URL = "https://eth-mainnet.alchemyapi.io/v2/"
        private val log = getLogger()

    }
}