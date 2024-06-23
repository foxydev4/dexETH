package io.boardfi.api.integrations.web3

import java.math.BigInteger

data class SwapV2(
    val pool: String,
    val amount0In: BigInteger,
    val amount1In: BigInteger,
    val amount0Out: BigInteger,
    val amount1Out: BigInteger,
    val reserve0 : BigInteger,
    val reserve1 : BigInteger,
    val token0: String,
    val token1: String,
)