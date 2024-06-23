package io.boardfi.api.integrations.alchemy.model


data class AlchemyMetadataResponse(val id: Int?, val jsonrpc: String?, val result: AlchemyMetadataResult?)
data class AlchemyMetadataResult(val decimals: Int?, val logo: String?, val name: String?, val symbol: String?)