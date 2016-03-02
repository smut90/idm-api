package net.cake.idm.core.exception;

/**
 * Exception to handle error scenarios in IDM related services
 *
 * @author Chamantha De Silva
 */
public class IDMException extends Exception {

    private static final long serialVersionUID = 3078289635627866758L;

    private final String errorCode;

    public IDMException(String errorCode, String message, Throwable t) {
        super(message, t);
        this.errorCode = errorCode;
    }

    public IDMException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
