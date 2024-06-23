package io.boardfi.api.server.boundary.inbound.document

data class TokenDocumentPage(
	val total: Long,
	val data: List<TokenDocument>,
)
