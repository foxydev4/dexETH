package io.boardfi.api.integrations.web3

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import kotlin.math.pow

@Service
class UniswapListener(private val uniswapV3Service: UniswapV3Service, private val uniswapV2Service: UniswapV2Service) :
    CommandLineRunner {

    private val web3j: Web3j = Web3j.build(HttpService(ALCHEMY_URL))
    override fun run(vararg args: String?) {
        val fromAddress = "0x3fc91a3afd70395cd496c647d5a6cc9d4b2b7fad"
        val toAddress = "0x3fc91a3afd70395cd496c647d5a6cc9d4b2b7fad"
        var retryCount = 0
        while (retryCount < 3) {
            try {
                web3j.blockFlowable(false).subscribe { ethBlock ->
                    println("Block number: ${ethBlock.block.number}")
                    val ethPrice = getWethPrice()
                    val block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(ethBlock.block.number), true).send()
                    block.block.transactions.forEach { transactionResult ->
                        val transaction = transactionResult as EthBlock.TransactionObject
                        if (transaction.from == fromAddress || transaction.to == toAddress) {
                            val receipt = web3j.ethGetTransactionReceipt(transaction.hash).send()
                            if (receipt.transactionReceipt.isPresent) {
                                val targetV3 = "0xc42079f94a6350d7e6235f29174924f928cc2ac818eb64fed8004e115fbcca67"
                                val targetV2 = "0xd78ad95fa46c994b6551d0da85fc275fe613ce37657fb8d5e3d130840159d822"
                                receipt.result.logs.forEach { log ->
                                    log.topics.forEach { topic ->
                                        if (topic == targetV3) {
                                            uniswapV3Service.getV3SwapEventsLogs(log, ethPrice)
                                        }
                                        if (topic == targetV2) {
                                            uniswapV2Service.getV2SwapEventsLogs(log, ethPrice)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break
            } catch (e: Exception) {
                retryCount++
                println("Error occurred: ${e.message}. Retrying... ($retryCount)")
                if (retryCount >= 3) {
                    println("Maximum retry attempts reached. Operation failed.")
                }
            }
        }
    }


    fun getWethPrice(): Double {
        val pairAddress = "0x0d4a11d5EEaaC28EC3F61d100daF4d40471f1852"
        val reservesFunction = FunctionEncoder.encode(
            Function(
                "getReserves", emptyList(), listOf(
                    TypeReference.create(
                        Address::class.java
                    )
                )
            )
        )

        val reservesResponse = web3j.ethCall(
            Transaction.createEthCallTransaction(null, pairAddress, reservesFunction), DefaultBlockParameterName.LATEST
        ).send()

        val reserves = reservesResponse.value
        val reserve0 = Numeric.toBigInt(reserves.substring(2, 66))
        val reserve1 = Numeric.toBigInt(reserves.substring(66, 130))

        return (reserve1.toDouble() / 10.0.pow(6.0)) / (reserve0.toDouble() / 10.0.pow(18.0))
    }

    companion object {
        private const val ALCHEMY_URL: String = "wss://eth-mainnet.g.alchemy.com/v2/lvQqa1KOxSyOzK7zm4GgbRS4JFPJcipc"
    }
}