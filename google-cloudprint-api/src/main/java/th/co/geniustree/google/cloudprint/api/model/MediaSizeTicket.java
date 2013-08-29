/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class MediaSizeTicket {

    private int width_microns;
    private int height_microns;
    private boolean is_continuous_feed;

    public int getWidth_microns() {
        return width_microns;
    }

    public void setWidth_microns(int width_microns) {
        this.width_microns = width_microns;
    }

    public int getHeight_microns() {
        return height_microns;
    }

    public void setHeight_microns(int height_microns) {
        this.height_microns = height_microns;
    }

    public boolean isIs_continuous_feed() {
        return is_continuous_feed;
    }

    public void setIs_continuous_feed(boolean is_continuous_feed) {
        this.is_continuous_feed = is_continuous_feed;
    }
}
