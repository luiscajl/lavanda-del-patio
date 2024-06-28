package es.lavanda.downloader.bt4g.exception;

public class Bt4gException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public Bt4gException(String message, Exception e) {
        super(message, e);
    }

    public Bt4gException(String message) {
        super(message);
    }

    public Bt4gException(Exception e) {
        super(e);
    }
}