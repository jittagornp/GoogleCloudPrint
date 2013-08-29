/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

import com.google.gson.Gson;
import java.util.Set;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class Job {

    private String createTime;
    private Set<String> tags;
    private String printerName;
    private String updateTime;
    private JobStatus status;
    private String ownerId;
    private String ticketUrl;
    private String printerid;
    private String printerType;
    private String contentType;
    private String fileUrl;
    private String id;
    private String message;
    private String title;
    private String errorCode;
    private int numberOfPages;
    //
    private Gson gson = new Gson();

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public void setStatus(String jobStatus) {
        this.status = JobStatus.valueOf(jobStatus);
        if(status == null){
            status = JobStatus.ERROR;
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public String getPrinterid() {
        return printerid;
    }

    public void setPrinterid(String printerid) {
        this.printerid = printerid;
    }

    public String getPrinterType() {
        return printerType;
    }

    public void setPrinterType(String printerType) {
        this.printerType = printerType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Job other = (Job) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{"
                + "\n\t createTime = " + createTime + ","
                + "\n\t tags = " + tags + ","
                + "\n\t printerName = " + printerName + ","
                + "\n\t updateTime = " + updateTime + ","
                + "\n\t status = " + status + ","
                + "\n\t ownerId = " + ownerId + ","
                + "\n\t ticketUrl = " + ticketUrl + ","
                + "\n\t printerid = " + printerid + ","
                + "\n\t printerType = " + printerType + ","
                + "\n\t contentType = " + contentType + ","
                + "\n\t fileUrl = " + fileUrl + ","
                + "\n\t id = " + id + ","
                + "\n\t message = " + message + ","
                + "\n\t title = " + title + ","
                + "\n\t errorCode = " + errorCode + ","
                + "\n\t numberOfPages = " + numberOfPages + ","
                + "\n\t tags = " + tags
                + "\n}";
    }

    public String toJson() {
        return gson.toJson(this);
    }
}
