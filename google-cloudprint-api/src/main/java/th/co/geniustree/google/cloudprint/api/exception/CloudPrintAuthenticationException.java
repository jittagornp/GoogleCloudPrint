/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.exception;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class CloudPrintAuthenticationException extends CloudPrintException {

    public CloudPrintAuthenticationException() {
        super();
    }

    public CloudPrintAuthenticationException(String message) {
        super(message);
    }

    public CloudPrintAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudPrintAuthenticationException(Throwable cause) {
        super(cause);
    }

    protected CloudPrintAuthenticationException(String message, Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
