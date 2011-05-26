package de.xzise.wrappers;

public class InvalidWrapperException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7540374362448021388L;

    public InvalidWrapperException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidWrapperException(String message) {
        super(message);
    }
    
    public InvalidWrapperException(Throwable cause) {
        super(cause);
    }
    
}
