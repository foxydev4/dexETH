package io.boardfi.api.integrations.web3

import java.math.BigInteger

data class SwapV3(
    val pool: String,
    val amount0: BigInteger,
    val amount1: BigInteger,
    val sqrtPriceX96: BigInteger,
    val liquidity: BigInteger,
    val tick: BigInteger,
    val token0: String,
    val token1: String,
)