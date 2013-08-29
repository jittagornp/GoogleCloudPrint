/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class SelectCapabilityOption {

    private String display_name;
    private String value;
    private boolean is_defaul;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isIs_defaul() {
        return is_defaul;
    }

    public void setIs_defaul(boolean is_defaul) {
        this.is_defaul = is_defaul;
    }
}
