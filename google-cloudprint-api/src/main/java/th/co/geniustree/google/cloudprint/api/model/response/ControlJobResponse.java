/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model.response;

import th.co.geniustree.google.cloudprint.api.model.Job;


/**
 *
 * @author jittagorn pitakmetagoon
 */
public class ControlJobResponse extends CloudPrintResponse{

    private Job job;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
