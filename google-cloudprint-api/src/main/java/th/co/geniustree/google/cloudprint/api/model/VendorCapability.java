/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class VendorCapability {

    private String id;
    private String display_name;
    private String type;
    private SelectCapability select_cap;
    private TypedValueCapability typed_value_cap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SelectCapability getSelect_cap() {
        return select_cap;
    }

    public void setSelect_cap(SelectCapability select_cap) {
        this.select_cap = select_cap;
    }

    public TypedValueCapability getTyped_value_cap() {
        return typed_value_cap;
    }

    public void setTyped_value_cap(TypedValueCapability typed_value_cap) {
        this.typed_value_cap = typed_value_cap;
    }
}
