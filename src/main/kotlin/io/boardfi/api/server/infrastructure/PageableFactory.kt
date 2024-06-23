package io.boardfi.api.server.infrastructure

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

fun pageable(page: Int, size: Int, sort: Sort? = null): Pageable {
	validate(page, size)
	return if (sort != null) PageRequest.of(page - 1, size, sort) else PageRequest.of(page - 1, size)
}

private fun validate(page: Int, size: Int) {
	ensureThat(page >= 1, "Page number must be greater than or equal to 1")
	ensureThat(size >= 1, "Page size must be greater than or equal to 1")
	ensureThat(size <= 1000, "Page size must not be greater than 1000")
}
