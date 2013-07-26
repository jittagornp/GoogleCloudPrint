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
public class PrinterCapability {

    private Copies copies;
    private List<SupportedContentType> supported_content_type;
    private Color color;
    private Collate collate;
    private MediaSize media_size;
    private PageOrientation page_orientation;
    private List<VendorCapability> vendor_capability;
    private DPI dpi;
    private Duplex duplex;

    public Copies getCopies() {
        return copies;
    }

    public void setCopies(Copies copies) {
        this.copies = copies;
    }

    public List<SupportedContentType> getSupported_content_type() {
        return supported_content_type;
    }

    public void setSupported_content_type(List<SupportedContentType> supported_content_type) {
        this.supported_content_type = supported_content_type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Collate getCollate() {
        return collate;
    }

    public void setCollate(Collate collate) {
        this.collate = collate;
    }

    public MediaSize getMedia_size() {
        return media_size;
    }

    public void setMedia_size(MediaSize media_size) {
        this.media_size = media_size;
    }

    public PageOrientation getPage_orientation() {
        return page_orientation;
    }

    public void setPage_orientation(PageOrientation page_orientation) {
        this.page_orientation = page_orientation;
    }

    public List<VendorCapability> getVendor_capability() {
        return vendor_capability;
    }

    public void setVendor_capability(List<VendorCapability> vendor_capability) {
        this.vendor_capability = vendor_capability;
    }

    public DPI getDpi() {
        return dpi;
    }

    public void setDpi(DPI dpi) {
        this.dpi = dpi;
    }

    public Duplex getDuplex() {
        return duplex;
    }

    public void setDuplex(Duplex duplex) {
        this.duplex = duplex;
    }
}
