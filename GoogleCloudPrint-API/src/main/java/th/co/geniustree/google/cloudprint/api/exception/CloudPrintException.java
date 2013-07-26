/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.exception;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class CloudPrintException extends Exception {

    public CloudPrintException() {
        super();
    }

    public CloudPrintException(String message) {
        super(message);
    }

    public CloudPrintException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudPrintException(Throwable cause) {
        super(cause);
    }

    protected CloudPrintException(String message, Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
