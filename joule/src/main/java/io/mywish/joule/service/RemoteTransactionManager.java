package io.mywish.joule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mywish.joule.dto.SignTransaction;
import io.mywish.joule.dto.SignedTransaction;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.TransactionManager;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;

@Slf4j
@Component
public class RemoteTransactionManager extends TransactionManager {
    private final LockService lockService;
    private final Web3j web3j;
    @Getter
    private final String fromAddress;
    private final URL url;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public RemoteTransactionManager(
            LockService lockService,
            Web3j web3j,
            @Value("${io.mywish.joule.tx.from-address}") String fromAddress,
            @Value("${io.mywish.joule.tx.sign.url}") URL url) {
        super(web3j, fromAddress);
        this.lockService = lockService;
        this.web3j = web3j;
        this.fromAddress = fromAddress;
        this.url = url;
        this.client = new OkHttpClient.Builder()
                .build();
        this.objectMapper = new ObjectMapper();
    }

    private BigInteger getNonce() throws IOException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                fromAddress, DefaultBlockParameterName.PENDING).send();

        return ethGetTransactionCount.getTransactionCount();
    }


    @Override
    public EthSendTransaction sendTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value
    ) throws IOException {
        return lockService.acquireLock(fromAddress)
                .thenApply(acquired -> {
                    if (!acquired) {
                        throw new RuntimeException("Waiting for address locking was timed out.");
                    }
                    try {
                        BigInteger nonce = getNonce();

                        SignTransaction signTransaction = new SignTransaction(
                                fromAddress,
                                to,
                                value,
                                gasLimit,
                                data.substring(2),
                                nonce
                        );


                        String requestBody = objectMapper.writeValueAsString(signTransaction);

                        Response response = client.newCall(new Request.Builder()
                                .url(url)
                                .post(RequestBody.create(HttpService.JSON_MEDIA_TYPE, requestBody))
                                .build())
                                .execute();
                        if (response == null) {
                            throw new RuntimeException("Sign was not returned response.");
                        }

                        if (response.body() == null) {
                            throw new RuntimeException("Sign returned empty body response.");
                        }

                        String responseBody = response.body().string();
                        SignedTransaction signedTransaction = objectMapper.readValue(responseBody, SignedTransaction.class);

                        return web3j
                                .ethSendRawTransaction("0x" + signedTransaction.getResult())
                                .send();
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Error on signing transaction.", e);
                    }
                })

                .exceptionally(e -> {
                    log.error("Error on singing transaction.", e);
                    return null;
                })
                .join();
    }
}
