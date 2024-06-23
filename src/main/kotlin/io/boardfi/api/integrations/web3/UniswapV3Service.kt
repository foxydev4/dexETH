package io.boardfi.api.integrations.web3

import io.boardfi.api.integrations.alchemy.AlchemyService
import io.boardfi.api.integrations.alchemy.model.AlchemyMetadataResponse
import io.boardfi.api.integrations.thegraph.TheGraphClient
import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenCurrentValueRepository
import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenDailyValueRepository
import io.boardfi.api.server.boundary.outbound.postgres.JpaTokenRepository
import io.boardfi.api.server.domain.token.PoolType
import io.boardfi.api.server.domain.token.Token
import io.boardfi.api.server.domain.token.TokenCurrentValue
import io.boardfi.api.server.domain.token.TokenDailyValue
import io.boardfi.api.server.infrastructure.getLogger
import jakarta.persistence.EntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt
import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import kotlin.math.abs

@Component
class UniswapV3Service(
    private val alchemyService: AlchemyService,
    private val jpaTokenRepository: JpaTokenRepository,
    private val tokenDailyValueRepository: JpaTokenDailyValueRepository,
    private val tokenCurrentValueRepository: JpaTokenCurrentValueRepository,
    private val entityManager: EntityManager,
    private val theGraphClient: TheGraphClient
) {

    private val web3j: Web3j = Web3j.build(HttpService(ALCHEMY_URL))
    private val log = getLogger()
    private val contractGasProvider = DefaultGasProvider();


    @Transactional
    fun getV3SwapEventsLogs(log: Log, wethPrice: Double) {
        val targetToken = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2"
        val swapV3 = decodeHexData(log.data, log.address)
        if (swapV3.token0 == targetToken || swapV3.token1 == targetToken) {
            createTokenFromSwap(swapV3, wethPrice)
        }
    }

    @Transactional
    fun createTokenFromSwap(swap: SwapV3, wethPrice: Double): Boolean {
        var tokenId: String
        var parAdj: Double
        var tokenPrice: Double
        var tokenAmmountSwapped: Double
        val isNew = false;
        if (swap.token1 != "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2") tokenId = swap.token1 else tokenId =
            swap.token0
        val metadata = alchemyService.fetchMetadata(tokenId)
        val decimals = metadata.result?.decimals?.toDouble() ?: 18.0
        val tokenToWeth = Math.pow((swap.sqrtPriceX96.toDouble() / Math.pow(2.0, 96.0)), 2.0)
        if (swap.token0 == "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2") {
            tokenAmmountSwapped = abs(swap.amount0.toDouble() / Math.pow(10.0, 18.0));
            parAdj = (Math.pow(10.0, decimals) / Math.pow(10.0, 18.0));
            tokenPrice = wethPrice / (tokenToWeth / parAdj)
        } else {
            tokenAmmountSwapped = abs(swap.amount1.toDouble() / Math.pow(10.0, 18.0));
            parAdj = (Math.pow(10.0, 18.0) / Math.pow(10.0, decimals));
            tokenPrice = wethPrice / (1 / (tokenToWeth / parAdj))
        }

        val token = jpaTokenRepository.findByAddress(tokenId) ?: createAndSaveToken(
            swap, tokenId, ERC20.load(tokenId, web3j, contractGasProvider).totalSupply().send(), metadata
        )

        // Ensure the Token entity is managed
        val managedToken = entityManager.merge(token)

        managedToken.run {
            val today = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
            var vol = tokenAmmountSwapped * wethPrice
            val existingTokenDailyValue = tokenDailyValueRepository.findAllByTokenId(
                this.id!!, PageRequest.of(0, 2000)
            ).content.find { it.timestamp.atZone(ZoneId.systemDefault()).toLocalDate() == today }
            val tokenDailyValue = existingTokenDailyValue?.copy(
                price = tokenPrice, volume = existingTokenDailyValue.volume + vol, timestamp = Instant.now()
            ) ?: TokenDailyValue(
                token = this, price = tokenPrice, volume = vol, timestamp = Instant.now()
            )

            tokenDailyValueRepository.save(tokenDailyValue)
            val existingTokenCurrentValue = tokenCurrentValueRepository.findById(token.id!!)
            val tokenCurrentValue = if (existingTokenCurrentValue.isPresent) {
                existingTokenCurrentValue.get().copy(
                    currentPrice = tokenPrice,
                    liquidity = 0.0,
                    circulatingSupply = supply,
                    volume = existingTokenCurrentValue.get().volume + vol.toLong(),
                    timestamp = Instant.now()
                )
            } else {
                TokenCurrentValue(
                    token = this,
                    currentPrice = tokenPrice,
                    price1h = 0.0,
                    price24h = 0.0,
                    price7d = 0.0,
                    marketCap = this.supply.toDouble() * tokenPrice,
                    liquidity = 0.0,
                    circulatingSupply = supply,
                    holders = 0,
                    volume = vol.toLong(),
                    timestamp = Instant.now()
                )
            }
            tokenCurrentValueRepository.save(tokenCurrentValue)

        }
        return isNew;
    }

    private fun addDailyTrades(token: Token) {
        val result = theGraphClient.fetchTokenDailyDataV3(token.address);
        result.forEach {
            val tokenDailyValue = TokenDailyValue(
                token = token,
                price = it.priceUSD.toString().toDouble(),
                volume = it.volumeUSD.toString().toDouble(),
                timestamp = Instant.ofEpochSecond(it.date.toLong())
            )
            if (tokenDailyValue.price != 0.0) {
                tokenDailyValueRepository.save(tokenDailyValue)
            }
        }
    }

    private fun createAndSaveToken(
        swap: SwapV3, tokenId: String, supply: BigInteger, metadata: AlchemyMetadataResponse
    ): Token {
        val existingToken = jpaTokenRepository.findByAddress(tokenId)
        val token = if (existingToken == null) {
            val newToken = Token(
                name = metadata.result?.name ?: "Unknown",
                poolId = swap.pool,
                poolToken0 = swap.token0,
                poolToken1 = swap.token1,
                poolType = PoolType.V3,
                address = tokenId,
                ticker = metadata.result?.symbol ?: "Unknown",
                logo = metadata.result?.logo ?: "Unknown",
                chain = "ethereum",
                creationTimestamp = Instant.now(),
                score = 0,
                paidListing = false,
                website = "Unknown",
                discord = "Unknown",
                telegram = "Unknown",
                twitter = "Unknown",
                decimals = metadata.result?.decimals ?: 18,
                supply = (supply / BigInteger.TEN.pow(metadata.result?.decimals ?: 18)).toString(),
            )
            jpaTokenRepository.save(newToken)
            addDailyTrades(newToken)
            newToken
        } else {
            existingToken
        }
        return token
    }

    fun decodeHexData(data: String, pool: String): SwapV3 {
        val cleanData = data.removePrefix("0x")
        val chunkSize = 64
        val chunks = cleanData.chunked(chunkSize)
        val values = chunks.map { chunk ->
            val value = BigInteger(chunk, 16)
            if (value.testBit(255)) value - BigInteger.ONE.shiftLeft(256) else value
        }

        val token0: String
        val token1: String

        val tokenDB = jpaTokenRepository.findByPoolId(pool)
        if (tokenDB != null) {
            token0 = tokenDB.poolToken0
            token1 = tokenDB.poolToken1
        } else {
            val tokenContracts = getTokenContracts(pool)
            token0 = tokenContracts.first
            token1 = tokenContracts.second
        }

        return SwapV3(
            pool = pool,
            amount0 = values[0],
            amount1 = values[1],
            sqrtPriceX96 = values[2],
            liquidity = values[3],
            tick = values[4],
            token0 = token0,
            token1 = token1,
        )
    }

    fun getTokenContracts(poolAddress: String): Pair<String, String> {
        val token0Function =
            FunctionEncoder.encode(Function("token0", emptyList(), listOf(TypeReference.create(Address::class.java))))
        val token1Function =
            FunctionEncoder.encode(Function("token1", emptyList(), listOf(TypeReference.create(Address::class.java))))

        val token0Response = web3j.ethCall(
            Transaction.createEthCallTransaction(null, poolAddress, token0Function), DefaultBlockParameterName.LATEST
        ).send()

        val token1Response = web3j.ethCall(
            Transaction.createEthCallTransaction(null, poolAddress, token1Function), DefaultBlockParameterName.LATEST
        ).send()

        val token0Address = Numeric.prependHexPrefix(token0Response.value.substring(26))
        val token1Address = Numeric.prependHexPrefix(token1Response.value.substring(26))

        return Pair(token0Address, token1Address)
    }

    companion object {
        private const val ALCHEMY_URL: String = "wss://eth-mainnet.g.alchemy.com/v2/lvQqa1KOxSyOzK7zm4GgbRS4JFPJcipc"
    }
}