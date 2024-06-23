import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	kotlin("plugin.jpa") version "1.9.22"
	id("com.apollographql.apollo3") version "3.8.2"
}

group = "io.boardfi"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// Serialization
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Database
	implementation("org.flywaydb:flyway-core")
	runtimeOnly("org.postgresql:postgresql")

	// Reflection
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// GraphQL
	implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")

	//Web3j
	implementation("org.web3j:core:5.0.0")

	// OpenAPI documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	// Utils
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("io.mockk:mockk:1.13.9")
	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.8.0")
	testImplementation("io.kotest.extensions:kotest-extensions-clock:1.0.0")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
	testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<BootBuildImage> {
	imageName = "boardfi-api"
}

apollo {
	service("uniswapV3") {
		srcDir("src/main/graphql/uniswapV3")
		schemaFile.set(file("src/main/graphql/uniswapV3/schemaV3.graphqls"))
		packageName.set("io.boardfi.api.integrations.v3")
	}

	service("uniswapV2") {
		srcDir("src/main/graphql/uniswapV2")
		schemaFile.set(file("src/main/graphql/uniswapV2/schemaV2.graphqls"))
		packageName.set("io.boardfi.api.integrations.v2")
	}
}
