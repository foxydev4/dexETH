package io.boardfi.api.server.infrastructure

fun ensureThat(condition: Boolean, message: String) {
	if (!condition) {
		throw BoardfiBadRequestException(message)
	}
}

fun ensureThat(condition: Boolean, supplier: () -> BoardfiException) {
	if (!condition) {
		throw supplier()
	}
}
