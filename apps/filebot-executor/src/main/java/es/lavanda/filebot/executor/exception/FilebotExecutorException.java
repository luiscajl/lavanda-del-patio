package es.lavanda.filebot.executor.exception;

public class FilebotExecutorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FilebotExecutorException(String message, Exception e) {
        super(message, e);
    }

    public FilebotExecutorException(String message) {
        super(message);
    }

    public FilebotExecutorException(Exception e) {
        super(e);
    }

}
