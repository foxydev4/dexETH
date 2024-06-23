package io.boardfi.api.server.infrastructure

import org.springdoc.core.models.GroupedOpenApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Configuration
@Controller
@ConditionalOnProperty(value = ["service.swagger.enabled"], matchIfMissing = true)
class SwaggerConfig {

	@GetMapping("/")
	fun handleRedirectToSwaggerDocsFromIndexPage(): String {
		return "redirect:/swagger-ui/index.html"
	}

	@Bean
	fun boardfi(): GroupedOpenApi = GroupedOpenApi.builder()
		.group("Boardfi API")
		.packagesToScan("io.boardfi.api.server.boundary")
		.pathsToMatch("/**")
		.build()
}
