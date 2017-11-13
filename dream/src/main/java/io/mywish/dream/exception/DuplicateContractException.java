package io.mywish.dream.exception;

public class DuplicateContractException extends RuntimeException {
    public DuplicateContractException() {
        super();
    }

    public DuplicateContractException(String message) {
        super(message);
    }

    public DuplicateContractException(String message, Throwable cause) {
        super(message, cause);
    }
}
