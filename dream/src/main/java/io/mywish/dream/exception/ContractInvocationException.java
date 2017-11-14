package io.mywish.dream.exception;

public class ContractInvocationException extends RuntimeException {
    public ContractInvocationException() {
    }

    public ContractInvocationException(String message) {
        super(message);
    }

    public ContractInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
