/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.exception;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class GoogleAuthenticationException extends Exception {

    public GoogleAuthenticationException() {
        super();
    }

    public GoogleAuthenticationException(String message) {
        super(message);
    }

    public GoogleAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleAuthenticationException(Throwable cause) {
        super(cause);
    }

    protected GoogleAuthenticationException(String message, Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
