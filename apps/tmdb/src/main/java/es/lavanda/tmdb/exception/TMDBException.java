package es.lavanda.tmdb.exception;

public class TMDBException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TMDBException(String message) {
        super(message);
    }

    public TMDBException(String message, Exception e) {
        super(message, e);
    }
}