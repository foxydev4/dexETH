package io.boardfi.api.server.domain.token

import jakarta.persistence.EntityManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TokenPriceSyncServiceJob(private val entityManager: EntityManager) {

    @Scheduled(cron = "0 0 * * * ?") // Every hour
    @Transactional
    fun copyPriceTo1h() {
        entityManager.createNativeQuery("update token_current_values set price_1h = current_price").executeUpdate()
    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    @Transactional
    fun copyPriceTo24h() {
        entityManager.createNativeQuery("update token_current_values set price_24h = current_price").executeUpdate()
    }

    @Scheduled(cron = "0 0 0 * * MON") // Every Monday at midnight
    @Transactional
    fun copyPriceTo7d() {
        entityManager.createNativeQuery("update token_current_values set price_7d = current_price").executeUpdate()
    }
}