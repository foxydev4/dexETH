package io.boardfi.api.server.domain.token

import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity(name = "tokens")
data class Token(
	@Id
	@GeneratedValue(strategy = IDENTITY)
	val id: Long? = null,

	@Column(nullable = false)
	val name: String,

	@Column(nullable = false, name = "pool_id")
	val poolId: String,

	@Column(nullable = false, name = "pool_token0")
	val poolToken0: String,

	@Column(nullable = false, name = "pool_token1")
	val poolToken1: String,

	@Column(nullable = false, name = "logo")
	val logo: String,

	@Enumerated(STRING)
	@Column(nullable = false, name = "pool_type")
	val poolType: PoolType,

	@Column(nullable = false)
	val address: String,

	@Column(nullable = false)
	val decimals: Int,

	@Column(nullable = false)
	val ticker: String,

	@Column(nullable = false)
	val chain: String,

	@Column(nullable = false, name = "creation_timestamp")
	val creationTimestamp: Instant,

	@Column(nullable = false)
	val score: Int,

	@Column(nullable = false, name = "paid_listing")
	val paidListing: Boolean = false,

	@Column(nullable = false)
	val website: String?,

	@Column(nullable = false)
	val discord: String?,

	@Column(nullable = false)
	val telegram: String?,

	@Column(nullable = false)
	val twitter: String?,

	@Column(nullable = false)
	val supply: String,

	@CreationTimestamp
	@Column(nullable = false, name = "created_at")
	val createdAt: Instant? = Instant.now(),

	@OneToOne(mappedBy = "token", fetch = LAZY, cascade = [ALL], orphanRemoval = true)
	val currentValue: TokenCurrentValue? = null,

	@OneToMany(mappedBy = "token", fetch = LAZY, cascade = [ALL])
	val dailyValues: MutableList<TokenDailyValue> = mutableListOf(),

	@OneToMany(mappedBy = "token", fetch = LAZY, cascade = [ALL])
	val recentSwaps: MutableList<TokenRecentSwap> = mutableListOf(),
)
