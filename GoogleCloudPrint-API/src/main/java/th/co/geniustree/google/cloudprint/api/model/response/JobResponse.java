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
public class JobResponse extends CloudPrintResponse {

    private List<Job> jobs;
    private int jobsCount;
    private String jobsTotal;

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public int getJobsCount() {
        return jobsCount;
    }

    public void setJobsCount(int jobsCount) {
        this.jobsCount = jobsCount;
    }

    public String getJobsTotal() {
        return jobsTotal;
    }

    public void setJobsTotal(String jobsTotal) {
        this.jobsTotal = jobsTotal;
    }
}
