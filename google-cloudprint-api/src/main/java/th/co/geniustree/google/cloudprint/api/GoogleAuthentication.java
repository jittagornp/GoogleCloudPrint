package th.co.geniustree.google.cloudprint.api;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import th.co.geniustree.google.cloudprint.api.util.ResponseUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import th.co.geniustree.google.cloudprint.api.exception.GoogleAuthenticationException;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class GoogleAuthentication {

    public static final String LOGIN_URL = "https://www.google.com/accounts/ClientLogin";
    private static final String ACCOUNT_TYPE = "HOSTED_OR_GOOGLE";
    //request by user
    private String serviceName;
    private String source;
    //response from google
    private String auth;
    private String sid;
    private String lsid;

    private GoogleAuthentication() {
    }

    public GoogleAuthentication(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * For login Google Service<br/>
     * <a href='https://developers.google.com/accounts/docs/AuthForInstalledApps'>https://developers.google.com/accounts/docs/AuthForInstalledApps</a>
     *
     * @param email Google Account or Google Email
     * @param password Email Password
     * @param source Short string identifying your application, for logging
     * purposes. This string take from :
     * "companyName-applicationName-VersionID".
     * @throws GoogleAuthenticationException
     */
    public void login(String email, String password, String source) throws GoogleAuthenticationException {
        InputStream inputStream = null;
        try {
            URL url = new URL(new StringBuilder().append(LOGIN_URL)
                    .append("?accountType=").append(ACCOUNT_TYPE)
                    .append("&Email=").append(email)
                    .append("&Passwd=").append(password)
                    .append("&service=").append(serviceName)
                    .append("&source=").append(source)
                    .toString());
            
            inputStream = url.openStream();
            String response = ResponseUtils.streamToString(inputStream);

            String[] split = response.split("\n");
            for (String string : split) {
                String[] keyValueSplit = string.split("=");
                if (keyValueSplit.length == 2) {
                    String key = keyValueSplit[0];
                    String value = keyValueSplit[1];

                    if (key.equalsIgnoreCase("Auth")) {
                        auth = value;
                    } else if (key.equalsIgnoreCase("SID")) {
                        sid = value;
                    } else if (key.equalsIgnoreCase("LSID")) {
                        lsid = value;
                    }
                }
            }
        } catch (IOException ex) {
            throw new GoogleAuthenticationException(ex);
        }finally{
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw new GoogleAuthenticationException(ex);
                }
            }
        }
    }

    public String getSource() {
        return source;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getAuth() {
        return auth;
    }

    public String getSid() {
        return sid;
    }

    public String getLsid() {
        return lsid;
    }
}
