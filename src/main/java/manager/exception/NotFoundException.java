package manager.exception;

/**
 * To be thrown when Task wasn't found
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

