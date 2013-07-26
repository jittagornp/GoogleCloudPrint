/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class MediaSizeOption {

    private int height_microns;
    private int width_microns;
    private String name;
    private boolean is_continuous_feed;
    private boolean is_default;

    public int getHeight_microns() {
        return height_microns;
    }

    public void setHeight_microns(int height_microns) {
        this.height_microns = height_microns;
    }

    public int getWidth_microns() {
        return width_microns;
    }

    public void setWidth_microns(int width_microns) {
        this.width_microns = width_microns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_continuous_feed() {
        return is_continuous_feed;
    }

    public void setIs_continuous_feed(boolean is_continuous_feed) {
        this.is_continuous_feed = is_continuous_feed;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }
}
