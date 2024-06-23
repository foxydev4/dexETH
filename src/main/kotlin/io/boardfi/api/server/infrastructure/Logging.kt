package io.boardfi.api.server.infrastructure

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.getLogger(): Logger {
	return LoggerFactory.getLogger(T::class.java)
}
