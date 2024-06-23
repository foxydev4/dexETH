package io.boardfi.api.integrations.web3

import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.tx.Contract
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigInteger

class ERC20(contractAddress: String, web3j: Web3j, gasProvider: ContractGasProvider) :
    Contract("", contractAddress, web3j, Credentials.create(ECKeyPair.create(BigInteger.ONE)), gasProvider) {
    fun totalSupply(): org.web3j.protocol.core.RemoteCall<BigInteger> {
        val function = Function(
            "totalSupply",
            emptyList<Type<*>>(),
            listOf<TypeReference<*>>(object : TypeReference<Uint256>() {})
        )
        return executeRemoteCallSingleValueReturn(function, BigInteger::class.java)
    }

    fun balanceOf(owner: String): org.web3j.protocol.core.RemoteCall<BigInteger> {
        val function = Function(
            "balanceOf",
            listOf(Address(owner)),
            listOf<TypeReference<*>>(object : TypeReference<Uint256>() {})
        )
        return executeRemoteCallSingleValueReturn(function, BigInteger::class.java)
    }

    companion object {
        fun load(contractAddress: String, web3j: Web3j, gasProvider: ContractGasProvider): ERC20 {
            return ERC20(contractAddress, web3j, gasProvider)
        }
    }
}