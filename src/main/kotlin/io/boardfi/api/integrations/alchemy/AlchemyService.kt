package io.boardfi.api.integrations.alchemy

import io.boardfi.api.integrations.alchemy.model.AlchemyMetadataResponse

interface AlchemyService {
    fun fetchMetadata(address: String): AlchemyMetadataResponse;
}