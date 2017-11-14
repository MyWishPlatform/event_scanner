package io.mywish.dream.model.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.1.0.
 */
public final class TicketHolder extends Contract {
    private static final String BINARY = "0x60606040908152600160a060020a0333166000908152602081905220805460ff191660011790556104dd806100356000396000f30060606040526004361061008d5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166314f2979f811461009257806385a2dd15146100de5780638823da6c14610105578063a76594bf14610126578063cecc33e71461014b578063cf12e87014610170578063d9fca7691461018f578063dd11247e146101ae575b600080fd5b341561009d57600080fd5b6100a86004356101de565b6040518084600160a060020a0316600160a060020a03168152602001838152602001828152602001935050505060405180910390f35b34156100e957600080fd5b6100f161025c565b604051901515815260200160405180910390f35b341561011057600080fd5b610124600160a060020a036004351661027c565b005b341561013157600080fd5b6101396102b0565b60405190815260200160405180910390f35b341561015657600080fd5b610124600160a060020a03600435166024356044356102b6565b341561017b57600080fd5b610124600160a060020a03600435166103d4565b341561019a57600080fd5b610124600160a060020a036004351661040b565b34156101b957600080fd5b6101c161045a565b60405167ffffffffffffffff909116815260200160405180910390f35b600080600080600380549050600014156101f757610254565b6003546000190185111561020a57610254565b600380548690811061021857fe5b6000918252602080832090910154600160a060020a0316808352600290915260409091208054600182015492965063ffffffff16945090925090505b509193909250565b600160a060020a03331660009081526020819052604090205460ff165b90565b61028461025c565b151561028f57600080fd5b600160a060020a03166000908152602081905260409020805460ff19169055565b60035490565b60006102c061025c565b15156102cb57600080fd5b6401000000008311156102da57fe5b50600160a060020a0383166000908152600260205260409020805463ffffffff1615156103755781151561030d57600080fd5b60038054825463ffffffff9091166401000000000267ffffffff0000000019909116178255805460018101610342838261046a565b506000918252602090912001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0386161790555b805463ffffffff19811663ffffffff9182168501821617808355168390101561039d57600080fd5b6001805467ffffffffffffffff80821686011667ffffffffffffffff1990911617905581156103ce57600181018290555b50505050565b6103dc61025c565b15156103e757600080fd5b600160a060020a03166000908152602081905260409020805460ff19166001179055565b600061041561025c565b151561042057600080fd5b50600160a060020a0381166000908152600260205260409020805463ffffffff16151561044c57600080fd5b805463ffffffff1916905550565b60015467ffffffffffffffff1681565b81548183558181151161048e5760008381526020902061048e918101908301610493565b505050565b61027991905b808211156104ad5760008155600101610499565b50905600a165627a7a72305820c740cd51346f33141e33eac1d96bf5a7476b29cc90ff8f6d81f1b1921e5d07920029";

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    private TicketHolder(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private TicketHolder(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<SaleFinishedEventResponse> getSaleFinishedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("SaleFinished", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<SaleFinishedEventResponse> responses = new ArrayList<SaleFinishedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            SaleFinishedEventResponse typedResponse = new SaleFinishedEventResponse();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<SaleFinishedEventResponse> saleFinishedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("SaleFinished", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, SaleFinishedEventResponse>() {
            @Override
            public SaleFinishedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                SaleFinishedEventResponse typedResponse = new SaleFinishedEventResponse();
                return typedResponse;
            }
        });
    }

    public RemoteCall<Tuple3<String, BigInteger, BigInteger>> getTickets(BigInteger index) {
        final Function function = new Function("getTickets", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<String, BigInteger, BigInteger>>(
                new Callable<Tuple3<String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple3<String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> hasAccess() {
        Function function = new Function("hasAccess", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> removeAccess(String _addr) {
        Function function = new Function(
                "removeAccess", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getPlayersCount() {
        Function function = new Function("getPlayersCount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> issueTickets(String _addr, BigInteger _ticketAmount, BigInteger _dreamAmount) {
        Function function = new Function(
                "issueTickets", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr), 
                new org.web3j.abi.datatypes.generated.Uint256(_ticketAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_dreamAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> giveAccess(String _addr) {
        Function function = new Function(
                "giveAccess", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setWinner(String _addr) {
        Function function = new Function(
                "setWinner", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalTickets() {
        Function function = new Function("totalTickets", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<TicketHolder> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TicketHolder.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<TicketHolder> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TicketHolder.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static TicketHolder load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TicketHolder(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TicketHolder load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TicketHolder(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class SaleFinishedEventResponse {
    }
}
