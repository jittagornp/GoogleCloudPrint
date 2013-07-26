/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class Capabilities {

    private PrinterCapability printer;
    private String version;

    public PrinterCapability getPrinter() {
        return printer;
    }

    public void setPrinter(PrinterCapability printer) {
        this.printer = printer;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
