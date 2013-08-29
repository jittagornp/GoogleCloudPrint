/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

import java.util.List;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class PrintTicket {

    private List<VendorTicketItem> vendor_ticket_item;
    private ColorTicket color;
    private DuplexTicket duplex;
    private PageOrientationTicket page_orientation;
    private CopiesTicket copies;
    private DPITicket dpi;
    private MediaSizeTicket media_size;
    private CollateTicket collate;

    public List<VendorTicketItem> getVendor_ticket_item() {
        return vendor_ticket_item;
    }

    public void setVendor_ticket_item(List<VendorTicketItem> vendor_ticket_item) {
        this.vendor_ticket_item = vendor_ticket_item;
    }

    public ColorTicket getColor() {
        return color;
    }

    public void setColor(ColorTicket color) {
        this.color = color;
    }

    public DuplexTicket getDuplex() {
        return duplex;
    }

    public void setDuplex(DuplexTicket duplex) {
        this.duplex = duplex;
    }

    public PageOrientationTicket getPage_orientation() {
        return page_orientation;
    }

    public void setPage_orientation(PageOrientationTicket page_orientation) {
        this.page_orientation = page_orientation;
    }

    public CopiesTicket getCopies() {
        return copies;
    }

    public void setCopies(CopiesTicket copies) {
        this.copies = copies;
    }

    public DPITicket getDpi() {
        return dpi;
    }

    public void setDpi(DPITicket dpi) {
        this.dpi = dpi;
    }

    public MediaSizeTicket getMedia_size() {
        return media_size;
    }

    public void setMedia_size(MediaSizeTicket media_size) {
        this.media_size = media_size;
    }

    public CollateTicket getCollate() {
        return collate;
    }

    public void setCollate(CollateTicket collate) {
        this.collate = collate;
    }
}
