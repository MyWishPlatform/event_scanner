package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.model.ContractTransactionFailedEvent;
import io.lastwill.eventscan.events.model.PendingTransactionAddedEvent;
import io.lastwill.eventscan.events.model.PendingTransactionRemovedEvent;
import io.lastwill.eventscan.messages.AirdropNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.model.AirdropEntry;
import io.lastwill.eventscan.model.ProductAirdrop;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.mywish.blockchain.WrapperOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AirdropPendingMonitor {
    private static final int TRANSFER_METHOD_ID = 0xffc3a769;
    private static final int TRANSFER_FROM_METHOD_ID = 0x46091499;
    private static final int MAX_ARRAY_LENGTH = 1024;
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private ExternalNotifier externalNotifier;

    private static List<AirdropEntry> parseInputToAirdrop(byte[] input) {
        ByteBuffer buffer = ByteBuffer.wrap(input);
        int methodId = buffer.getInt();
        if (methodId != TRANSFER_METHOD_ID && methodId != TRANSFER_FROM_METHOD_ID) {
            log.warn("Wrong method input script, expected {} or {}, but got {}.", TRANSFER_FROM_METHOD_ID, TRANSFER_METHOD_ID, methodId);
            return Collections.emptyList();
        }
        if (methodId == TRANSFER_FROM_METHOD_ID) {
            // skip address parameter if "transferFrom" called
            buffer.position(buffer.position() + 0x20);
        }

        // data location of first
        buffer.position(buffer.position() + 0x20);
        // data location of second
        buffer.position(buffer.position() + 0x20);

        byte[] bytes32 = new byte[32];
        byte[] bytes20 = new byte[20];

        buffer.get(bytes32);
        BigInteger addressesLength = new BigInteger(bytes32);
        if (addressesLength.compareTo(BigInteger.ZERO) <= 0) {
            log.warn("Addresses array length is less or equals zero.");
            return Collections.emptyList();
        }
        if (addressesLength.intValue() > MAX_ARRAY_LENGTH) {
            log.warn("Addresses array length is too big: max {}, but got {}.", MAX_ARRAY_LENGTH, addressesLength);
            return Collections.emptyList();
        }
        String[] addresses = new String[addressesLength.intValue()];
        for (int i = 0; i < addresses.length; i++) {
            buffer.get(bytes32);
            bytes32toBytes20(bytes32, bytes20);
            addresses[i] = "0x" + DatatypeConverter.printHexBinary(bytes20).toLowerCase();
        }

        buffer.get(bytes32);
        BigInteger valuesLength = new BigInteger(bytes32);
        if (valuesLength.compareTo(BigInteger.ZERO) <= 0) {
            log.warn("Values array length is less or equals zero.");
            return Collections.emptyList();
        }
        if (valuesLength.intValue() > MAX_ARRAY_LENGTH) {
            log.warn("Values array length is too big: max {}, but got {}.", MAX_ARRAY_LENGTH, valuesLength);
            return Collections.emptyList();
        }
        if (valuesLength.compareTo(addressesLength) != 0) {
            log.warn("Wrong input script structure: arrays length mismatch {} != {}.", addressesLength, valuesLength);
            return Collections.emptyList();
        }

        List<AirdropEntry> result = new ArrayList<>(valuesLength.intValue());
        for (int i = 0; i < valuesLength.intValue(); i++) {
            buffer.get(bytes32);
            BigInteger value = new BigInteger(bytes32);
            result.add(new AirdropEntry(addresses[i], value));
        }

        return result;
    }

    private static void bytes32toBytes20(byte[] bytes32, byte[] bytes20) {
        System.arraycopy(bytes32, 12, bytes20, 0, 20);
    }

    @EventListener
    public void handlePendingAdded(final PendingTransactionAddedEvent event) {
        if (event.getTransaction().isContractCreation()) {
            return;
        }

        Set<String> addresses =
                event.getTransaction().getOutputs()
                        .stream()
                        .map(WrapperOutput::getAddress)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
        if (addresses.isEmpty()) {
            return;
        }

        contractRepository.findByAddressesList(addresses, event.getNetworkType())
                .stream()
                .filter(contract -> contract.getProduct() instanceof ProductAirdrop)
                .forEach(contract ->
                        event.getTransaction()
                                .getOutputs()
                                .forEach(wrapperOutput -> {
                                    if (wrapperOutput.getAddress().compareToIgnoreCase(contract.getAddress()) != 0) {
                                        return;
                                    }
                                    try {

                                        List<AirdropEntry> values =
                                                parseInputToAirdrop(wrapperOutput.getRawOutputScript());
                                        externalNotifier.send(
                                                event.getNetworkType(),
                                                new AirdropNotify(
                                                        contract.getId(),
                                                        PaymentStatus.PENDING,
                                                        event.getTransaction().getHash(),
                                                        values
                                                )
                                        );
                                    }
                                    catch (Exception e) {
                                        log.warn("Parsing input airdrop invocation was failed.", e);
                                    }
                                }));
    }

    @EventListener
    public void handlePendingRemoved(final PendingTransactionRemovedEvent event) {
        // only timed out
        if (event.getReason() != PendingTransactionRemovedEvent.Reason.TIMEOUT) {
            return;
        }
        if (event.getTransaction().isContractCreation()) {
            return;
        }

        Set<String> addresses =
                event.getTransaction().getOutputs()
                        .stream()
                        .map(WrapperOutput::getAddress)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());

        if (addresses.isEmpty()) {
            return;
        }

        contractRepository.findByAddressesList(addresses, event.getNetworkType())
                .stream()
                .filter(contract -> contract.getProduct() instanceof ProductAirdrop)
                .forEach(contract ->
                        event.getTransaction()
                                .getOutputs()
                                .forEach(wrapperOutput -> {
                                    if (wrapperOutput.getAddress().compareToIgnoreCase(contract.getAddress()) != 0) {
                                        return;
                                    }
                                    try {
                                        List<AirdropEntry> values =
                                                parseInputToAirdrop(wrapperOutput.getRawOutputScript());
                                        externalNotifier.send(
                                                event.getNetworkType(),
                                                new AirdropNotify(
                                                        contract.getId(),
                                                        PaymentStatus.REJECTED,
                                                        event.getTransaction().getHash(),
                                                        values
                                                )
                                        );
                                    }
                                    catch (Exception e) {
                                        log.warn("Parsing input airdrop invocation was failed.", e);
                                    }
                                }));

    }

    @EventListener
    private void handleContractFailed(ContractTransactionFailedEvent event) {
        if (!(event.getContract().getProduct() instanceof ProductAirdrop)) {
            return;
        }

        event.getTransaction()
                .getOutputs()
                .forEach(wrapperOutput -> {
                    if (wrapperOutput.getAddress().compareToIgnoreCase(event.getContract().getAddress()) != 0) {
                        return;
                    }
                    try {
                        List<AirdropEntry> values =
                                parseInputToAirdrop(wrapperOutput.getRawOutputScript());
                        externalNotifier.send(
                                event.getNetworkType(),
                                new AirdropNotify(
                                        event.getContract().getId(),
                                        PaymentStatus.REJECTED,
                                        event.getTransaction().getHash(),
                                        values
                                )
                        );
                    }
                    catch (Exception e) {
                        log.warn("Parsing input airdrop invocation was failed.", e);
                    }
                });
    }
}
