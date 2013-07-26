/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model.response;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class CloudPrintResponse {

    private boolean success;
    private String message;
    private String xsrf_token;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getXsrf_token() {
        return xsrf_token;
    }

    public void setXsrf_token(String xsrf_token) {
        this.xsrf_token = xsrf_token;
    }
}
