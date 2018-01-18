package io.mywish.joule.service;

import io.lastwill.eventscan.model.Product;
import io.mywish.joule.contracts.JouleAPI;
import io.mywish.joule.model.JouleRegistration;
import io.mywish.joule.model.JouleRegistrationState;
import io.mywish.joule.repositories.JouleRegistrationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.time.ZoneOffset;

@Slf4j
@Component
public class RegistrationService {
    @Autowired
    private JouleRegistrationRepository jouleRegistrationRepository;

    @Autowired
    private JouleAPI jouleAPI;

    @Value("${io.mywish.joule.registration.gas-price}")
    private BigInteger registrationGasPrice;

    @Value("${io.mywish.contract.invocation.gas-price}")
    private BigInteger invocationGasPrice;

    @Value("${io.mywish.joule.registration.gas-limit}")
    private BigInteger registrationGasLimit;

    @Scheduled(fixedDelay = 1000)
    protected void checkReadyToRegister() {
        jouleRegistrationRepository.findByStateOrderByIdAsc(JouleRegistrationState.CREATED)
                .forEach(this::doRegistration);

    }

    private void doRegistration(JouleRegistration jouleRegistration) {
        if (!lockToSend(jouleRegistration)) {
            log.debug("Registration {} already locked.", jouleRegistration);
            return;
        }
        Product product = jouleRegistration.getContract().getProduct();
        TransactionReceipt receipt;
        try {
            BigInteger price = jouleAPI.getPrice(product.getCheckGasLimit(), registrationGasPrice).send();
            receipt = jouleAPI.register(
                    jouleRegistration.getContract().getAddress(),
                    BigInteger.valueOf(jouleRegistration.getInvocationAt().toEpochSecond(ZoneOffset.UTC)),
                    registrationGasLimit,
                    registrationGasPrice,
                    price
            ).send();
        }
        catch (Exception e) {
            log.warn("Registration failed. Skip registration {}.", jouleRegistration, e);
            return;
        }
        unlock(jouleRegistration, receipt.getTransactionHash());
    }

    private boolean lockToSend(JouleRegistration jouleRegistration) {
        int affected = jouleRegistrationRepository.updateState(
                jouleRegistration,
                JouleRegistrationState.CREATED,
                JouleRegistrationState.LOCKED
        );

        if (affected == 1) {
            return true;
        }
        if (affected > 1) {
            log.warn("updateState returned more the one ({}) affected record", affected);
        }
        return false;
    }

    private void unlock(JouleRegistration jouleRegistration, String txHash) {
        int affected = jouleRegistrationRepository.updateTxHashAndState(
                jouleRegistration,
                txHash,
                JouleRegistrationState.LOCKED,
                JouleRegistrationState.TX_PUBLISHED
        );

        if (affected == 0) {
            log.warn("Update txHash to {} was not passed (no rows affected).", jouleRegistration);
        }
    }
}
