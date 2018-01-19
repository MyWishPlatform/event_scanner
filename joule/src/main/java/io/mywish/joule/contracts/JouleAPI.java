package io.mywish.joule.contracts;

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
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
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
public final class JouleAPI extends Contract {
    private static final String BINARY = null;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<>();
    }

    private JouleAPI(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private JouleAPI(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<InvokedEventResponse> getInvokedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Invoked", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<InvokedEventResponse> responses = new ArrayList<InvokedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            InvokedEventResponse typedResponse = new InvokedEventResponse();
            typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._usedGas = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<InvokedEventResponse> invokedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Invoked", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, InvokedEventResponse>() {
            @Override
            public InvokedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                InvokedEventResponse typedResponse = new InvokedEventResponse();
                typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._status = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._usedGas = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<RegisteredEventResponse> getRegisteredEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Registered", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<RegisteredEventResponse> responses = new ArrayList<RegisteredEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            RegisteredEventResponse typedResponse = new RegisteredEventResponse();
            typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._timestamp = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._gasLimit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse._gasPrice = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RegisteredEventResponse> registeredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Registered", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RegisteredEventResponse>() {
            @Override
            public RegisteredEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                RegisteredEventResponse typedResponse = new RegisteredEventResponse();
                typedResponse._address = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._timestamp = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._gasLimit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse._gasPrice = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> invokeTop() {
        Function function = new Function(
                "invokeTop", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<List<String>, List<BigInteger>, List<BigInteger>, List<BigInteger>>> getTop(BigInteger _count) {
        final Function function = new Function("getTop", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_count)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}, new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteCall<Tuple4<List<String>, List<BigInteger>, List<BigInteger>, List<BigInteger>>>(
                new Callable<Tuple4<List<String>, List<BigInteger>, List<BigInteger>, List<BigInteger>>>() {
                    @Override
                    public Tuple4<List<String>, List<BigInteger>, List<BigInteger>, List<BigInteger>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple4<List<String>, List<BigInteger>, List<BigInteger>, List<BigInteger>>(
                                (List<String>) results.get(0).getValue(), 
                                (List<BigInteger>) results.get(1).getValue(), 
                                (List<BigInteger>) results.get(2).getValue(), 
                                (List<BigInteger>) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> register(String _address, BigInteger _timestamp, BigInteger _gasLimit, BigInteger _gasPrice, BigInteger weiValue) {
        Function function = new Function(
                "register", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_address), 
                new org.web3j.abi.datatypes.generated.Uint256(_timestamp), 
                new org.web3j.abi.datatypes.generated.Uint256(_gasLimit), 
                new org.web3j.abi.datatypes.generated.Uint256(_gasPrice)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<Tuple4<String, BigInteger, BigInteger, BigInteger>> getTop() {
        final Function function = new Function("getTop", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<String, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple4<String, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<String, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple4<String, BigInteger, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> getPrice(BigInteger _gasLimit, BigInteger _gasPrice) {
        Function function = new Function("getPrice", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_gasLimit), 
                new org.web3j.abi.datatypes.generated.Uint256(_gasPrice)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> invoke() {
        Function function = new Function(
                "invoke", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<JouleAPI> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(JouleAPI.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<JouleAPI> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(JouleAPI.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static JouleAPI load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new JouleAPI(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static JouleAPI load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new JouleAPI(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class InvokedEventResponse {
        public String _address;

        public Boolean _status;

        public BigInteger _usedGas;
    }

    public static class RegisteredEventResponse {
        public String _address;

        public BigInteger _timestamp;

        public BigInteger _gasLimit;

        public BigInteger _gasPrice;
    }
}
