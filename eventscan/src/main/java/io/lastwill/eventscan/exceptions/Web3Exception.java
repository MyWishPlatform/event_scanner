package io.lastwill.eventscan.exceptions;

import org.web3j.protocol.core.Response;

public class Web3Exception extends Exception {
    public Web3Exception(Response.Error error) {
        super(createMessage(error));
    }

    public Web3Exception(Response.Error error, Throwable cause) {
        super(createMessage(error), cause);
    }

    private static String createMessage(Response.Error error) {
        return "Error returned (" + error.getCode() + "): " + error.getMessage() + ". Data: " + error.getData();
    }
}
