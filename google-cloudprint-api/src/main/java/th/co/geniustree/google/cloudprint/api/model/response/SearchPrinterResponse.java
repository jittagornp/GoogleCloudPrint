/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model.response;

import java.util.List;
import th.co.geniustree.google.cloudprint.api.model.Printer;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class SearchPrinterResponse extends CloudPrintResponse{

    private List<Printer> printers;

    public List<Printer> getPrinters() {
        return printers;
    }

    public void setPrinters(List<Printer>  printers) {
        this.printers = printers;
    }
}
