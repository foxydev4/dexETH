package io.boardfi.api.server.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant
import java.time.format.DateTimeFormatterBuilder

@Configuration
class JacksonConfig {

	@Bean
	fun objectMapper(): ObjectMapper {
		val mapper = ObjectMapper()
		val javaTimeModule = JavaTimeModule().apply {
			addSerializer(Instant::class.java, InstantSerializerWithMillisecondPrecision())
		}
		mapper.registerModule(javaTimeModule)
		return mapper
	}
}

class InstantSerializerWithMillisecondPrecision :
	InstantSerializer(INSTANCE, false, false, DateTimeFormatterBuilder().appendInstant(3).toFormatter())

