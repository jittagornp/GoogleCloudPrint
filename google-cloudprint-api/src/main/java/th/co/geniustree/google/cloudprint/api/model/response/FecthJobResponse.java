/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model.response;

import java.util.List;
import th.co.geniustree.google.cloudprint.api.model.Job;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class FecthJobResponse extends CloudPrintResponse{

    private List<Job> jobs;
    private String errorCode;


    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
