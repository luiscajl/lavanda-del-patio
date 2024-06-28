package es.lavanda.lib.common.exception;

public class HandlerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(String message, Exception e) {
        super(message, e);
    }

}