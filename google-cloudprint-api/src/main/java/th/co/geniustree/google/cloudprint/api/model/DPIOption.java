/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class DPIOption {

    private int vertical_dpi;
    private boolean is_default;
    private int horizontal_dpi;

    public int getVertical_dpi() {
        return vertical_dpi;
    }

    public void setVertical_dpi(int vertical_dpi) {
        this.vertical_dpi = vertical_dpi;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public int getHorizontal_dpi() {
        return horizontal_dpi;
    }

    public void setHorizontal_dpi(int horizontal_dpi) {
        this.horizontal_dpi = horizontal_dpi;
    }
}
