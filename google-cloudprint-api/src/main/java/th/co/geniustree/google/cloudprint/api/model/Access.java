/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class Access {

    private String membership;
    private String email;
    private String name;
    private String role;
    private String USER;
    private boolean is_pending;

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public boolean isIs_pending() {
        return is_pending;
    }

    public void setIs_pending(boolean is_pending) {
        this.is_pending = is_pending;
    }

    @Override
    public String toString() {
        return "{"
                + "\n\t membership = " + membership + ","
                + "\n\t email = " + email + ","
                + "\n\t name = " + name + ","
                + "\n\t role = " + role + ","
                + "\n\t USER=" + USER + ","
                + "\n\t is_pending=" + is_pending
                + "\n}";
    }
}
