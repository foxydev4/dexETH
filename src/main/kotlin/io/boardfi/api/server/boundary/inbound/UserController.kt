package io.boardfi.api.server.boundary.inbound

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

@Tag(name = "User")
@Validated
@RestController
@RequestMapping("/api/user")
class EthereumController() {

    private val web3j: Web3j = Web3j.build(HttpService(ALCHEMY_URL))

    @Operation(summary = "Verify Ethereum balance")
    @GetMapping("/{address}")
    fun verifyBalance(
        @Parameter(
            description = "Wallet Address", schema = Schema(type = "string", example = "test")
        ) @PathVariable(required = true) address: String
    ): Boolean {

        val ethGetBalance: EthGetBalance =
            web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get()

        val balanceInWei: BigInteger = ethGetBalance.balance
        val balanceInEther: BigDecimal = Convert.fromWei(balanceInWei.toString(), Convert.Unit.ETHER)

        return balanceInEther >= BigDecimal("0.01")
    }

    companion object {
        private const val ALCHEMY_URL: String = "wss://eth-mainnet.g.alchemy.com/v2/kaC3lmlJnsRFdKcxtx_r-pGp4hZzmqD0"
    }

}