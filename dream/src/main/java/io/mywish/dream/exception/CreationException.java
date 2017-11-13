package io.mywish.dream.exception;

public class CreationException extends RuntimeException {
    public CreationException() {
        super();
    }

    public CreationException(String message) {
        super(message);
    }

    public CreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
