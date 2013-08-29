/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

import com.google.gson.Gson;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class Printer {

    private String id;
    private String ownerId;
    private String name;
    private String proxy;
    private String description;
    private String status;
    private PrinterStatus connectionStatus;
    private String supportedContentTypes;
    private String createTime;
    private String updateTime;
    private String accessTime;
    private String type;
    private String gcpVersion;
    private String capsHash;
    private boolean isTosAccepted;
    private String defaultDisplayName;
    private String displayName;
    private Set<String> tags;
    private Object capabilities;
    private Object defaults;
    private List<Access> access;
    private String capsFormat;
    //
    private Gson gson = new Gson();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PrinterStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(PrinterStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = PrinterStatus.valueOf(connectionStatus);
        if (this.connectionStatus == null) {
            this.connectionStatus = PrinterStatus.UNKNOWN;
        }
    }

    public String getSupportedContentTypes() {
        return supportedContentTypes;
    }

    public void setSupportedContentTypes(String supportedContentTypes) {
        this.supportedContentTypes = supportedContentTypes;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGcpVersion() {
        return gcpVersion;
    }

    public void setGcpVersion(String gcpVersion) {
        this.gcpVersion = gcpVersion;
    }

    public String getCapsHash() {
        return capsHash;
    }

    public void setCapsHash(String capsHash) {
        this.capsHash = capsHash;
    }

    public boolean isIsTosAccepted() {
        return isTosAccepted;
    }

    public void setIsTosAccepted(boolean isTosAccepted) {
        this.isTosAccepted = isTosAccepted;
    }

    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }

    public void setDefaultDisplayName(String defaultDisplayName) {
        this.defaultDisplayName = defaultDisplayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getTagsJSON() {
        return gson.toJson(tags);
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Object getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Object capabilities) {
        this.capabilities = capabilities;
    }

    public Object getDefaults() {
        return defaults;
    }

    public void setDefaults(Object defaults) {
        this.defaults = defaults;
    }

    public List<Access> getAccess() {
        return access;
    }

    public void setAccess(List<Access> access) {
        this.access = access;
    }

    public String getCapsFormat() {
        return capsFormat;
    }

    public void setCapsFormat(String capsFormat) {
        this.capsFormat = capsFormat;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Printer other = (Printer) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "{"
                + "\n\t id = " + id + ","
                + "\n\t ownerId = " + ownerId + ","
                + "\n\t name = " + name + ","
                + "\n\t proxy = " + proxy + ","
                + "\n\t description = " + description + ","
                + "\n\t status = " + status + ","
                + "\n\t connectionStatus = " + connectionStatus + ","
                + "\n\t supportedContentTypes = " + supportedContentTypes + ","
                + "\n\t createTime = " + createTime + ","
                + "\n\t updateTime = " + updateTime + ","
                + "\n\t accessTime = " + accessTime + ","
                + "\n\t type = " + type + ","
                + "\n\t gcpVersion = " + gcpVersion + ","
                + "\n\t capsHash = " + capsHash + ","
                + "\n\t isTosAccepted = " + isTosAccepted + ","
                + "\n\t defaultDisplayName = " + defaultDisplayName + ","
                + "\n\t displayName = " + displayName + ","
                + "\n\t tags = " + tags + ","
                + "\n\t capabilities = " + capabilities + ","
                + "\n\t defaults = " + defaults + ","
                + "\n\t access = " + access
                + "\n}";
    }

    public String toJson() {
        return gson.toJson(this);
    }
}
