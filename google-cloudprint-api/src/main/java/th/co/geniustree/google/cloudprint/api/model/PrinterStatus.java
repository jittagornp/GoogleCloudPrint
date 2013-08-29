/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 * connectionStatus: printer's connection status, which can be one of the
 * following:<br/><br/>
 * <b>ONLINE</b>: The printer has an active XMPP connection to Google Cloud
 * Print.<br/>
 * <b>UNKNOWN</b>: The printer's connection status cannot be determined.<br/>
 * <b>OFFLINE</b>: The printer is offline.<br/>
 * <b>DORMANT</b>: The printer has been offline for quite a while.<br/>
 * <b>ALL</b> : Match all printers.
 *
 * @author jittagorn pitakmetagoon
 */
public enum PrinterStatus {

    /**
     * <b>ONLINE</b>: The printer has an active XMPP connection to Google Cloud
     * Print.
     */
    ONLINE,
    /**
     * <b>UNKNOWN</b>: The printer's connection status cannot be determined.
     */
    UNKNOWN,
    /**
     * <b>OFFLINE</b>: The printer is offline.
     */
    OFFLINE,
    /**
     * <b>DORMANT</b>: The printer has been offline for quite a while.
     */
    DORMANT,
    /**
     * <b>ALL</b> : Match all printers.
     */
    ALL
}