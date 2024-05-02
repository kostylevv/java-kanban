package manager.exception;

/**
 * To be thrown when Task overlaps with other existing task
 */

public class OverlapException extends RuntimeException {
    public OverlapException(String message) {
        super(message);
    }
}

