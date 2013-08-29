/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

/**
 * Status of the job, which can be one of the following:<br/><br/>
 * <b>QUEUED</b>: Job just added and has not yet been downloaded.<br/>
 * <b>IN_PROGRESS</b>: Job downloaded and has been added to the client-side
 * native printer queue.<br/>
 * <b>DONE</b>: Job printed successfully.<br/>
 * <b>ERROR</b>: Job cannot be printed due to an error.
 *
 * @author jittagorn pitakmetagoon
 */
public enum JobStatus {

    /**
     * <b>QUEUED</b>: Job just added and has not yet been downloaded.
     */
    QUEUED,
    /**
     * <b>IN_PROGRESS</b>: Job downloaded and has been added to the client-side
     * native printer queue.
     */
    IN_PROGRESS,
    /**
     * <b>DONE</b>: Job printed successfully.
     */
    DONE,
    /**
     * <b>ERROR</b>: Job cannot be printed due to an error.
     */
    ERROR
}
