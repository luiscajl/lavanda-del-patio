package es.lavanda.indexers.exception;

public class IndexerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IndexerException(String message, Exception e) {
        super(message, e);
    }

    public IndexerException(String message) {
        super(message);
    }

    public IndexerException(Exception e) {
        super(e);
    }
}