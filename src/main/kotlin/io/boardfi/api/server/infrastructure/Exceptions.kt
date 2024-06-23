package io.boardfi.api.server.infrastructure

open class BoardfiException(message: String?) : RuntimeException(message)
class BoardfiBadRequestException(message: String?) : BoardfiException(message)
class BoardfiNotFoundException(message: String?) : BoardfiException(message)
class BoardfiInternalException(message: String?) : BoardfiException(message)
