package th.co.geniustree.google.cloudprint.api;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import th.co.geniustree.google.cloudprint.api.util.ResponseUtils;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import th.co.geniustree.google.cloudprint.api.exception.CloudPrintAuthenticationException;
import th.co.geniustree.google.cloudprint.api.exception.GoogleAuthenticationException;
import th.co.geniustree.google.cloudprint.api.exception.CloudPrintException;
import th.co.geniustree.google.cloudprint.api.model.response.ControlJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.DeletePrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.FecthJobResponse;
import th.co.geniustree.google.cloudprint.api.model.Job;
import th.co.geniustree.google.cloudprint.api.model.JobListener;
import th.co.geniustree.google.cloudprint.api.model.JobStatus;
import th.co.geniustree.google.cloudprint.api.model.Printer;
import th.co.geniustree.google.cloudprint.api.model.PrinterStatus;
import th.co.geniustree.google.cloudprint.api.model.RoleShare;
import th.co.geniustree.google.cloudprint.api.model.response.SearchPrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.SubmitJob;
import th.co.geniustree.google.cloudprint.api.model.response.DeleteJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.JobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.ListPrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.PrinterInformationResponse;
import th.co.geniustree.google.cloudprint.api.model.response.RegisterPrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SharePrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SubmitJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.UpdatePrinterResponse;

/**
 *
 * @author jittagorn pitakmetagoon
 * @see https://developers.google.com/cloud-print/
 */
public class GoogleCloudPrint {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleCloudPrint.class);
    //
    private static final String CLOUD_PRINT_URL = "https://www.google.com/cloudprint";
    private static final String GOOGLE_TALK_URL = "talk.google.com";
    private static final int GOOGLE_TALK_PORT = 5222;
    private static final String GOOGLE_TALK_SERVICE = "gmail.com";
    //
    private static final String CLOUD_PRINT_SERVICE = "cloudprint";
    private static final String FILTER_PACKAGE_FROM = "cloudprint.google.com";
    //
    private GoogleAuthentication authen;
    private XMPPConnection xmppConnection;
    private Gson gson;
    private List<JobListener> jobListeners;
    //

    public GoogleCloudPrint() {
        gson = new Gson();
        jobListeners = new ArrayList<JobListener>();
    }

    /**
     * For connect to Google Cloud Print Service and Google Talk for real time
     * job notify<br/><br/>
     *
     * @param email Google Account or Email Address
     * @param password Email Password
     * @param source Short string identifying your application, for logging
     * purposes. This string take from :
     * "companyName-applicationName-VersionID".
     * @throws CloudPrintAuthenticationException
     */
    public void connect(String email, String password, String source) throws CloudPrintAuthenticationException{
        try {
            //Google Cloud Print Service Authen
            authen = new GoogleAuthentication(CLOUD_PRINT_SERVICE);
            authen.login(email, password, source);
            //
            //Google Talk XMPP Authen
            ConnectionConfiguration config = new ConnectionConfiguration(GOOGLE_TALK_URL, GOOGLE_TALK_PORT, GOOGLE_TALK_SERVICE);
            xmppConnection = new XMPPConnection(config);
            xmppConnection.connect();
            xmppConnection.login(email, password);

            LOG.info("Connected to {}", GoogleAuthentication.LOGIN_URL + "[" + CLOUD_PRINT_SERVICE + "] ...");
            LOG.info("Connected to {}", GOOGLE_TALK_URL + ":" + GOOGLE_TALK_PORT + "[" + GOOGLE_TALK_SERVICE + "] ...");
            LOG.info("Start job listener from {}", GOOGLE_TALK_URL + ":" + GOOGLE_TALK_PORT + "[" + GOOGLE_TALK_SERVICE + "] ...");
            //
            listenerJob(email);
        } catch (XMPPException ex) {
            throw new CloudPrintAuthenticationException(ex);
        } catch (GoogleAuthenticationException ex) {
            throw new CloudPrintAuthenticationException(ex);
        }
    }

    /**
     * disconnect google talk notification
     */
    public void disconnect() {
        LOG.info("Disconnect from {}", GOOGLE_TALK_URL + ":" + GOOGLE_TALK_PORT + "[" + GOOGLE_TALK_SERVICE + "] ...");
        xmppConnection.disconnect();
    }

    private void listenerJob(String email) {
        final Pattern pattern = Pattern.compile("<data>(.+?)</data>");
        xmppConnection.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                boolean success = false;
                String message = null;
                //
                Matcher matcher = pattern.matcher(packet.toXML());
                matcher.find();
                String data = matcher.group(1);
                FecthJobResponse fecthResponse = null;

                try {
                    String printerId = new String(Base64.decodeBase64(data.getBytes(Charset.forName("UTF-8"))), "UTF-8");
                    fecthResponse = fetchJob(printerId);
                    success = true;
                } catch (Exception ex) {
                    success = false;
                    message = ex.getMessage();
                    LOG.warn(null, ex);
                } finally {
                    if (fecthResponse != null) {
                        List<Job> jobs = fecthResponse.getJobs();
                        if (fecthResponse.isSuccess() && !jobs.isEmpty()) {
                            for (Job job : jobs) {
                                for (JobListener jobListener : jobListeners) {
                                    jobListener.onJobArrive(job, fecthResponse.isSuccess(), fecthResponse.getMessage());
                                }
                            }
                        } else if (!fecthResponse.isSuccess()) {
                            for (JobListener jobListener : jobListeners) {
                                jobListener.onJobArrive(null, fecthResponse.isSuccess(), fecthResponse.getErrorCode() + " : " + fecthResponse.getMessage());
                            }
                        }
                    } else {
                        if (!success) {
                            for (JobListener jobListener : jobListeners) {
                                jobListener.onJobArrive(null, success, message);
                            }
                        }
                    }
                }
            }
        }, new AndFilter(new PacketTypeFilter(Message.class), new FromContainsFilter(FILTER_PACKAGE_FROM)));

        IQ iq = new IQ() {
            @Override
            public String getChildElementXML() {
                return new StringBuilder().append("<subscribe xmlns=\"google:push\">")
                        .append("<item channel=\"cloudprint.google.com\" from=\"cloudprint.google.com\"/>")
                        .append("</subscribe>")
                        .toString();
            }
        };

        iq.setType(IQ.Type.SET);
        iq.setTo(email);

        xmppConnection.sendPacket(iq);
    }

    /**
     * open connection to target service
     *
     * @param serviceAndParameters
     * @return service response
     * @throws CloudPrintException
     */
    private String openConnection(String serviceAndParameters) throws CloudPrintException {
        return openConnection(serviceAndParameters, null);
    }

    /**
     * open connection to target service
     *
     * @param serviceAndParameters
     * @param entity
     * @return service response
     * @throws CloudPrintException
     */
    private String openConnection(String serviceAndParameters, MultipartEntity entity) throws CloudPrintException {
        String response = "";
        HttpPost httpPost = null;
        InputStream inputStream = null;
        try {
            String request = CLOUD_PRINT_URL + serviceAndParameters;
            HttpClient httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(request);
            httpPost.setHeader("X-CloudPrint-Proxy", authen.getSource());
            httpPost.setHeader("Authorization", "GoogleLogin auth=" + authen.getAuth());

            if (entity != null) {
                httpPost.setEntity(entity);
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            response = ResponseUtils.streamToString(inputStream);
        } catch (Exception ex) {
            throw new CloudPrintException(ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            if (httpPost != null && !httpPost.isAborted()) {
                httpPost.abort();
            }

            return response;
        }
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#search'>/search</a></b><br/>
     * The /search interface returns a list of printers accessible to the
     * authenticated authenticated user, taking an optional search query as a
     * parameter. Note that by default, /search will not include printers that
     * have been offline for a long time (i.e. whose connectionStatus is
     * DORMANT). This behavior can be overridden if ALL or DORMANT is passed as
     * the value of the optional connection_status parameter.<br/><br/>
     * <b>Response</b><br/>
     * The response is a list of printers, in JSON. All fields are listed,
     * except for capabilities which must be retrieved using a call to
     * /printerCapabilities.
     *
     * @return SearchPrinterResponse
     * @throws CloudPrintException
     */
    public SearchPrinterResponse searchPrinters() throws CloudPrintException {
        String response = openConnection("/search?output=json&use_cdd=true");
        return gson.fromJson(new StringReader(response), SearchPrinterResponse.class);
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#search'>/search</a></b><br/>
     * The /search interface returns a list of printers accessible to the
     * authenticated authenticated user, taking an optional search query as a
     * parameter. Note that by default, /search will not include printers that
     * have been offline for a long time (i.e. whose connectionStatus is
     * DORMANT). This behavior can be overridden if ALL or DORMANT is passed as
     * the value of the optional connection_status parameter.<br/><br/>
     * <b>Response</b><br/>
     * The response is a list of printers, in JSON. All fields are listed,
     * except for capabilities which must be retrieved using a call to
     * /printerCapabilities.
     *
     * @param query (optional) If q is specified, then only printers
     * corresponding to the query will be returned. If q is not specified, then
     * all printers accessible (owned or shared with) the authenticated user
     * will be returned. The API looks for an approximate match between q and
     * the name and tag fields (ie. [field] == %q%). Thus, setting q = "^recent"
     * will return the list of recently used printers. Setting q = ^own or q =
     * ^shared" will return the list of printers either owned by or shared with
     * this user.
     *
     * @param status (optional) If connection_status is specified, then only
     * printers whose connection status matches the supplied value will be
     * returned. You may provide one of the four values listed above or you may
     * specify ALL, which will match all printers, including those marked as
     * DORMANT.
     *
     * @return SearchPrinterResponse
     * @throws CloudPrintException
     */
    public SearchPrinterResponse searchPrinter(String query, PrinterStatus status) throws CloudPrintException {
        String response = openConnection(new StringBuilder().append("/search?output=json")
                .append("&q=").append(query)
                .append("&connection_status=").append(status)
                .toString());

        return gson.fromJson(new StringReader(response), SearchPrinterResponse.class);
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#jobs'>/jobs</a></b><br/>
     * The /jobs interface retrieves the status of print jobs for a
     * user.<br/><br/>
     * <b>Response</b><br/>
     * The response is a list of print jobs, in JSON. Fields listed are the
     * following:<br/>
     * - <b>id</b> print job ID<br/>
     * - <b>printerid</b> printer’s GCP ID<br/>
     * - <b>title</b> document title<br/>
     * - <b>contentType</b> document content type (MIME type)<br/>
     * - <b>fileUrl</b><br/>
     * - <b>ticketUrl</b><br/>
     * - <b>createTime</b> time stamp of when the print job was created<br/>
     * - <b>updateTime</b> time stamp of when the print job status was last
     * updated<br/>
     * - <b>status</b> print job status (see below for a list of possible
     * status)<br/>
     * - <b>errorCode</b> contains an error code if the print job status is
     * ‘ERROR’, and otherwise is null<br/>
     * - <b>message</b><br/>
     * - <b>tags</b>
     * Print jobs are tagged as ^own if the user is the owner of the job, and
     * ^shared if the job was merely shared with the user.<br/><br/>
     *
     * Note that the status of a print job can be any of the following:<br/>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;QUEUED - Job just added
     * and has not yet been downloaded<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IN_PROGRESS - Job
     * downloaded and has been added to the client-side native printer
     * queue<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DONE - Job printed
     * successfully<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ERROR - Job cannot be
     * printed due to an error<br/>
     *
     * @return JobResponse
     * @throws CloudPrintException
     */
    public JobResponse getJobs() throws CloudPrintException {
        String response = openConnection("/jobs?output=json");
        return gson.fromJson(new StringReader(response), JobResponse.class);
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#jobs'>/jobs</a></b><br/>
     * The /jobs interface retrieves the status of print jobs for a
     * user.<br/><br/>
     * <b>Response</b><br/>
     * The response is a list of print jobs, in JSON. Fields listed are the
     * following:<br/>
     * - <b>id</b> print job ID<br/>
     * - <b>printerid</b> printer’s GCP ID<br/>
     * - <b>title</b> document title<br/>
     * - <b>contentType</b> document content type (MIME type)<br/>
     * - <b>fileUrl</b><br/>
     * - <b>ticketUrl</b><br/>
     * - <b>createTime</b> time stamp of when the print job was created<br/>
     * - <b>updateTime</b> time stamp of when the print job status was last
     * updated<br/>
     * - <b>status</b> print job status (see below for a list of possible
     * status)<br/>
     * - <b>errorCode</b> contains an error code if the print job status is
     * ‘ERROR’, and otherwise is null<br/>
     * - <b>message</b><br/>
     * - <b>tags</b>
     * Print jobs are tagged as ^own if the user is the owner of the job, and
     * ^shared if the job was merely shared with the user.<br/><br/>
     *
     * Note that the status of a print job can be any of the following:<br/>
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;QUEUED - Job just added
     * and has not yet been downloaded<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;IN_PROGRESS - Job
     * downloaded and has been added to the client-side native printer
     * queue<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DONE - Job printed
     * successfully<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ERROR - Job cannot be
     * printed due to an error<br/>
     *
     * @param printerId (optional) If a printer ID is specified, the print jobs
     * for that printer will be retrieved (instead of retrieving the jobs for
     * the user whose session this is).
     *
     * @return JobResponse
     * @throws CloudPrintException
     */
    public JobResponse getJobOfPrinter(String printerId) throws CloudPrintException {
        String response = openConnection("/jobs?output=json&printerid=" + printerId);
        return gson.fromJson(new StringReader(response), JobResponse.class);
    }

    /**
     * <b>/fetch</b><br/>
     * This interface is used to fetch the next available job for the specified
     * printer.<br/><br/>
     * <b>Response</b><br/>
     * The response object contains a Boolean success indicator and a list of
     * jobs. The list would contain only the jobs that are in QUEUED state.<br/>
     * <br/>
     * <b>Simple Response</b><br/>
     * {<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;"success": true,<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;"jobs": [{ <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"id":
     * "3432682791683548017",<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"title":
     * "CloudPrint_Architecture.pdf",<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"status": "QUEUED", <br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"fileUrl":"http://docs.google.com/printing/download?id\u003d3432682791683548017",<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ticketUrl":"http://docs.google.com/printing/fetch?output\u003dxml\u0026jobid\u003d3432682791683548017"<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;}]<br/> }<br/><br/>
     * <b>Note</b>: The URLs for downloading the file/data to be printed and the
     * print job ticket are absolute URLs, and they might not point to the same
     * host as the URL for the /fetch interface.<br/><br/>
     *
     * The ticketUrl field in the response points to a job ticket that can be in
     * XPS (<a
     * href='http://en.wikipedia.org/wiki/Open_XML_Paper_Specification'>XML
     * Paper Specification</a>) or PPD (<a
     * href='http://en.wikipedia.org/wiki/PostScript_Printer_Description'>Postscript
     * Printer Description</a>) format. In the future, other job ticket formats
     * may be supported. The fileUrl field in the response points to the data to
     * be printed. As the file download is an HTTP request, the printer /
     * software connector should specify MIME types it can accept to print in
     * the Accept header as defined by HTTP protocol. Google Cloud Print will
     * try to convert the print data to a format acceptable to the printer /
     * software connector.<br/><br/>
     *
     * When performing a request to fetch the fileUrl as a PWG-raster document,
     * we also support an optional query parameter skippages. When skippages=N
     * is specified, the first N pages of the PWG-raster document are skipped.
     * Page skipping is relative to the PWG-raster document after page
     * reordering and not relative to the original document. This parameter can
     * be used by the printer if it needs to resume printing pages from some
     * point, say after a paper jam. The parameter is for PWG-raster documents
     * only and has no effect if you download documents in PDF format.
     *
     * @param printerId Unique printer identification (generated by Google Cloud
     * Print).
     * @return FecthJobResponse
     * @throws CloudPrintException
     */
    public FecthJobResponse fetchJob(String printerId) throws CloudPrintException {
        String response = openConnection("/fetch?output=json&printerid=" + printerId);
        return gson.fromJson(new StringReader(response), FecthJobResponse.class);
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#printer'>/printer</a></b><br/>
     * The /printer interface retrieves the capabilities of the specified
     * printer.<br/></br><br/>
     * <b>Reponse</b><br/>
     * The response is a list of attributes about the requested printer, in
     * JSON. Fields returned include:<br/>
     * - <b>name</b><br/>
     * - <b>description</b><br/>
     * - <b>proxy</b> connector through which this printer is run, if any<br/>
     * - <b>status</b><br/>
     * - <b>tags</b><br/>
     * - <b>capabilities</b><br/>
     * - <b>access</b> a list of access roles<br/>
     * - <b>connectionStatus</b> printer's connection status, which can be on of
     * {
     *
     * @see PrinterStatus}<br/><br/>
     * Note that this field is only returned if printer_connection_status is set
     * to true.<br/>
     *
     * @param printerId The ID of the printer whose capabilities we require. The
     * printer must be accessible (either owned or shared with) the user
     * authenticated by the current authenticated session.
     *
     * @return PrinterInformationResponse
     * @throws CloudPrintException
     */
    public PrinterInformationResponse getPrinterInformation(String printerId) throws CloudPrintException {
        String response = openConnection("/printer?output=json&use_cdd=true&printerid=" + printerId);
        return gson.fromJson(new StringReader(response), PrinterInformationResponse.class);
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#printer'>/printer</a></b><br/>
     * The /printer interface retrieves the capabilities of the specified
     * printer.<br/></br><br/>
     * <b>Reponse</b><br/>
     * The response is a list of attributes about the requested printer, in
     * JSON. Fields returned include:<br/>
     * - <b>name</b><br/>
     * - <b>description</b><br/>
     * - <b>proxy</b> connector through which this printer is run, if any<br/>
     * - <b>status</b><br/>
     * - <b>tags</b><br/>
     * - <b>capabilities</b><br/>
     * - <b>access</b> a list of access roles<br/>
     * - <b>connectionStatus</b> printer's connection status, which can be on of
     * {
     *
     * @see PrinterStatus}<br/><br/>
     * Note that this field is only returned if printer_connection_status is set
     * to true.<br/>
     *
     * @param printerId The ID of the printer whose capabilities we require. The
     * printer must be accessible (either owned or shared with) the user
     * authenticated by the current authenticated session.
     *
     * @param status A Boolean that specifies whether or not to return the
     * printer's connectionStatus field.
     *
     * @return
     * @throws CloudPrintException
     */
    public PrinterInformationResponse getPrinterInformation(String printerId, PrinterStatus status) throws CloudPrintException {
        return getPrinterInformation(printerId + "&printer_connection_status=" + status);
    }

    /**
     * For download file from google cloud print when job arrive<br/>
     *
     * @param fileUrl from job.getFileUrl() when job arrive(job notify) or other
     * @param outputFile must is pdf file (.pdf only)
     * @throws CloudPrintException
     */
    public void downloadFile(String fileUrl, File outputFile) throws CloudPrintException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputFile);
            downloadFile(fileUrl, outputStream);
        } catch (Exception ex) {
            throw new CloudPrintException(ex);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }
        }
    }

    /**
     * For download file from google cloud print when job arrive<br/>
     *
     * @param fileUrl from job.getFileUrl() when job arrive(job notify) or other
     * @param outputStream (NOT CLOSE OutputStream)
     * @throws CloudPrintException
     */
    public void downloadFile(String fileUrl, OutputStream outputStream) throws CloudPrintException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("X-CloudPrint-Proxy", authen.getSource());
            connection.addRequestProperty("Content-Length", fileUrl.getBytes().length + "");
            connection.addRequestProperty("Authorization", "GoogleLogin auth=" + authen.getAuth());
            inputStream = connection.getInputStream();

            ByteStreams.copy(inputStream, outputStream);
        } catch (Exception ex) {
            throw new CloudPrintException(ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#submit'>/submit</a></b><br/>
     * The /submit service interface is used to send print jobs to the GCP
     * service. Upon initialization, the status of the print job will be QUEUED.
     * The print job is created and the appropriate printer is notified of its
     * existence. The status of the print job can then be tracked using /jobs,
     * as described below.<br/><br/>
     *
     * @param submitJob has attribute following : <br/><br/>
     * <b>printerid</b> Unique printer identification (generated by Google Cloud
     * Print). To get valid printer IDs for a given user, retrieve the list of
     * available printers for that Google Account by querying the /search
     * service interface.<br/><br/>
     * <b>title</b> Title of the print job, to be used within GCP.<br/><br/>
     * <b>capabilities</b> Printer capabilities (XPS or PPD). Each GCP printer
     * has, associated with it, a list of pair-value capabilities representing
     * printer-specific attributes (available printing formats, copy count,
     * etc.) Capabilities for a given printer can be retrieved using the /list
     * service interface. These retrieved capabilities can then be used to
     * specify desired options on the print job (for instance, print 5 copies
     * instead of the default 1, or print duplex instead of single
     * sided).<br/><br/>
     * <b>content</b> Document to print.<br/><br/>
     * <b>contentType</b> Document type. Currently, valid document types are:
     * url, application/pdf, image/jpeg, or image/png. If contentType = url, the
     * URL should point to a publicly accessible page (there should be no
     * necessary authentication, cookies, etc.) The linked resource can be a
     * PDF, JPG, or PNG file, but we recommend PDF for highest
     * fidelity.<br/><br/>
     * <b>tag</b> One or more tags to add to the print job. You can attach a set
     * of unique tags to a print job, and these will be available to the printer
     * to which the print job is submitted. This feature may be useful if your
     * application both sends and receives print jobs.<br/>
     *
     * @return SubmitJobResponse
     * @throws CloudPrintException
     */
    public SubmitJobResponse submitJob(SubmitJob submitJob) throws CloudPrintException {
        ByteArrayInputStream byteArrayInputStream = null;
        InputStream inputStream = null;
        String response = "";
        try {
            byteArrayInputStream = new ByteArrayInputStream(submitJob.getContent());
            InputStreamBody inputStreamBody = new InputStreamBody(byteArrayInputStream, submitJob.getContentType(), submitJob.getTitle());

            MultipartEntity entity = new MultipartEntity();
            entity.addPart("content", inputStreamBody);
            entity.addPart("contentType", new StringBody(submitJob.getContentType()));
            entity.addPart("title", new StringBody(submitJob.getTitle()));
            entity.addPart("ticket", new StringBody(submitJob.getTicketJSON()));

            if (submitJob.getTag() != null) {
                for (String tag : submitJob.getTag()) {
                    entity.addPart("tag", new StringBody(tag));
                }
            }
            response = openConnection("/submit?output=json&printerid=" + submitJob.getPrinterId(), entity);
        } catch (Exception ex) {
            throw new CloudPrintException(ex);
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            return gson.fromJson(new StringReader(response), SubmitJobResponse.class);
        }
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/proxyinterfaces#delete'>/delete</a></b><br/>
     * This interface is used to delete a printer from Google Cloud
     * Print.<br/><br/>
     * Response<br/>
     * The response object contains a Boolean success indicator and a
     * message.<br/>
     *
     * @param printerId Unique printer identification (generated by Google Cloud
     * Print).
     * @return DeletePrinterResponse
     * @throws CloudPrintException
     */
    public DeletePrinterResponse deletePrinter(String printerId) throws CloudPrintException {
        String response = openConnection("/delete?output=json&printerid=" + printerId);
        return gson.fromJson(new StringReader(response), DeletePrinterResponse.class);
    }

    /**
     * For listener google cloud print job notification
     *
     * @param jobListener
     */
    public void addJobListener(JobListener jobListener) {
        jobListeners.add(jobListener);
    }

    /**
     *
     * @return jobListeners (list of JobListener)
     */
    public List<JobListener> getJobListeners() {
        return jobListeners;
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/appInterfaces#deletejob'>/deletejob</a></b><br/></br>
     * The /deletejob interface deletes the given print job.<br/><br/>
     * <b>Response</b><br/>
     * The response object contains a Boolean success indicator and a message.
     *
     * @param jobId The ID of the print job to be deleted. The print job must be
     * owned by the user authenticated by the current authenticated session.
     * @return DeleteJobResponse
     * @throws CloudPrintException
     */
    public DeleteJobResponse deleteJob(String jobId) throws CloudPrintException {
        String response = openConnection("/deletejob?output=json&jobid=" + jobId);
        return gson.fromJson(new StringReader(response), DeleteJobResponse.class);
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/proxyinterfaces#control'>/control</a></b><br/>
     * This interface can be used by the printer / software connector to update
     * Google Cloud Print about the status of the print job on the printer
     * device. The code and message parameters are useful for displaying helpful
     * information to the user via the user interface. These parameters are not
     * used for any control, disabling, or filtering of the print job or the
     * printer.<br/><br/>
     *
     * @param jobid Unique job identification (generated by server).
     * @param status {
     * @see JobStatus}
     * @param code Error code string or integer (as returned by the printer or
     * OS) if the status is ERROR.
     * @param message Error message string (as returned by the printer or OS) if
     * the status is ERROR
     * @return The response object contains a Boolean success indicator and a
     * message.
     * @throws CloudPrintException
     */
    public ControlJobResponse controlJob(String jobid, JobStatus status, int code, String message) throws CloudPrintException {
        String response = openConnection(new StringBuilder().append("/control?output=json")
                .append("&jobid=").append(jobid)
                .append("&status=").append(status)
                .append("&code=").append(code)
                .append("&message=").append(message)
                .toString());

        return gson.fromJson(new StringReader(response), ControlJobResponse.class);
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/proxyinterfaces#update'>/update</a></b><br/>
     * This interface is used to update various attributes and parameters of the
     * printer registered with Google Cloud Print.<br/><br/>
     *
     * Parameters {
     *
     * @see Printer} require attribute following :<br/><br/>
     *
     * <b>printerid</b> Unique printer identification (generated by Google Cloud
     * Print).<br/>
     * <b>printer</b> User readable name of the printer (need not be
     * unique).<br/>
     * <b>display_name</b> User-customizable name of the printer (need not be
     * unique).<br/>
     * <b>proxy</b> Identification of the printer client or proxy (must be
     * unique).<br/>
     * <b>capabilities</b> Printer capabilities (XPS or PPD).<br/>
     * <b>defaults</b> Printer default settings (XPS or PPD).<br/>
     * <b>tag</b> One or more tags to add to the print job. You can attach a set
     * of unique tags to a print job, and these will be available to the printer
     * to which the print job is submitted. This feature may be useful if your
     * application both sends and receives print jobs.<br/>
     * <b>status</b> Status string of the printer—e.g., Out of Paper, Online,
     * etc.<br/>
     * <b>description</b> Descriptive string about the printer.<br/>
     * <b>capsHash</b> A hash or digest value of the capabilities data. This
     * value is useful, for example, to compare values and check whether the
     * local printer's capabilities have changed.<br/><br/>
     * <b>Response</b><br/>
     * The response object contains a Boolean success indicator and a
     * message.<br/>
     *
     * @param printer
     * @return UpdatePrinterResponse
     * @throws CloudPrintException
     */
    public UpdatePrinterResponse updatePrinter(Printer printer) throws CloudPrintException {
        String response = "";
        InputStream capabilitiesInputStream = null;
        InputStream defaultInputStream = null;
        try {
            MultipartEntity entity = new MultipartEntity();
            if (isNotNullAndEmpty(printer.getName())) {
                entity.addPart("printer", new StringBody(printer.getName()));
            }

            if (isNotNullAndEmpty(printer.getDisplayName())) {
                entity.addPart("display_name", new StringBody(printer.getDisplayName()));
            }

            if (isNotNullAndEmpty(printer.getProxy())) {
                entity.addPart("proxy", new StringBody(printer.getProxy()));
            }

            if (isNotNullAndIsFile(printer.getCapabilities())) {
                File capabilitiesFile = (File) printer.getCapabilities();
                capabilitiesInputStream = new FileInputStream(capabilitiesFile);
                InputStreamBody capabilitiesInputStreamBody = new InputStreamBody(capabilitiesInputStream, capabilitiesFile.getName());
                entity.addPart("capabilities", capabilitiesInputStreamBody);
            }

            if (isNotNullAndIsFile(printer.getDefaults())) {
                File defaultFile = (File) printer.getDefaults();
                defaultInputStream = new FileInputStream(defaultFile);
                InputStreamBody defaultInputStreamBody = new InputStreamBody(defaultInputStream, defaultFile.getName());
                entity.addPart("defaults", defaultInputStreamBody);
            }

            if (printer.getTags() != null) {
                for (String tag : printer.getTags()) {
                    entity.addPart("tag", new StringBody(tag));
                }
            }

            if (isNotNullAndEmpty(printer.getStatus())) {
                entity.addPart("status", new StringBody(printer.getStatus()));
            }

            if (isNotNullAndEmpty(printer.getDescription())) {
                entity.addPart("description", new StringBody(printer.getDescription()));
            }

            if (isNotNullAndEmpty(printer.getCapsHash())) {
                entity.addPart("capsHash", new StringBody(printer.getCapsHash()));
            }

            response = openConnection("/update?output=json&printerid=" + printer.getId(), entity);
        } catch (UnsupportedEncodingException ex) {
            throw new CloudPrintException(ex);
        } finally {
            if (capabilitiesInputStream != null) {
                try {
                    capabilitiesInputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            if (defaultInputStream != null) {
                try {
                    defaultInputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            return gson.fromJson(new StringReader(response), UpdatePrinterResponse.class);
        }
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/proxyinterfaces#register'>/register</a></b><br/>
     * This interface is used to register printers. This should be an HTTP
     * multipart request, since the request needs to upload significantly large
     * printer capabilities and defaults file data. The capabilities and
     * defaults parameters can be in XPS (XML Paper Specification :
     * http://en.wikipedia.org/wiki/Open_XML_Paper_Specification) or PPD
     * (Postscript Printer Description :
     * http://en.wikipedia.org/wiki/PostScript_Printer_Description) formats.
     * Additional formats may be supported in the future to describe the printer
     * capabilities and defaults.<br/><br/>
     *
     * Parameters {
     *
     * @see Printer} require attribute following :<br/><br/>
     *
     * <b>printer</b> User readable name of the printer being registered (need
     * not be unique).<br/>
     * <b>proxy</b> Identification of the printer client or proxy (must be
     * unique).<br/>
     * <b>capabilities</b> Printer capabilities (XPS or PPD).<br/>
     * <b>defaults</b> Printer default settings (XPS or PPD).<br/>
     * <b>tag</b> One or more tags to add to the print job. You can attach a set
     * of unique tags to a print job, which may be useful to store additional
     * metadata about the printer, for later use by your application.<br/>
     * <b>status</b> Status string of the printer—e.g., Out of Paper, Online,
     * etc.<br/>
     * <b>description</b> Descriptive string about the printer.<br/>
     * <b>capsHash</b> A hash or digest value of the capabilities data. This
     * value is useful, for example, to compare values and check whether the
     * local printer's capabilities have changed.<br/><br/>
     *
     * Response<br/>
     * The response object contains a Boolean success indication and a list of
     * printers that contains only one printer object, which describes the
     * printer added in the request.<br/>
     *
     * @param printer
     * @return RegisterPrinterResponse
     * @throws CloudPrintException
     */
    public RegisterPrinterResponse registerPrinter(Printer printer) throws CloudPrintException {
        String response = "";
        InputStream capabilitiesInputStream = null;
        InputStream defaultInputStream = null;
        try {
            if (printer.getName() == null) {
                throw new CloudPrintException("Require attribute name.");
            }

            if (printer.getProxy() == null) {
                throw new CloudPrintException("Require attribute proxy.");
            }

            if (!isNotNullAndIsFile(printer.getCapabilities())) {
                throw new CloudPrintException("Require attribute capability is File.");
            }

            if (!isNotNullAndIsFile(printer.getDefaults())) {
                throw new CloudPrintException("Require attribute defualts is File.");
            }

            if (printer.getTags() == null) {
                throw new CloudPrintException("Require attribute tags.");
            }

            if (printer.getStatus() == null) {
                throw new CloudPrintException("Require attribute status.");
            }

            if (printer.getDescription() == null) {
                throw new CloudPrintException("Require attribute description.");
            }

            if (printer.getCapsHash() == null) {
                throw new CloudPrintException("Require attribute capsHash.");
            }

            File capabilitiesFile = (File) printer.getCapabilities();
            File defaultFile = (File) printer.getDefaults();

            capabilitiesInputStream = new FileInputStream(capabilitiesFile);
            defaultInputStream = new FileInputStream(defaultFile);

            InputStreamBody capabilitiesInputStreamBody = new InputStreamBody(capabilitiesInputStream, capabilitiesFile.getName());
            InputStreamBody defaultInputStreamBody = new InputStreamBody(defaultInputStream, defaultFile.getName());

            MultipartEntity entity = new MultipartEntity();
            entity.addPart("printer", new StringBody(printer.getName()));
            entity.addPart("proxy", new StringBody(printer.getProxy()));
            entity.addPart("capabilities", capabilitiesInputStreamBody);
            entity.addPart("defaults", defaultInputStreamBody);

            for (String tag : printer.getTags()) {
                entity.addPart("tag", new StringBody(tag));
            }

            entity.addPart("status", new StringBody(printer.getStatus()));
            entity.addPart("description", new StringBody(printer.getDescription()));
            entity.addPart("capsHash", new StringBody(printer.getCapsHash()));

            response = openConnection("/register?output=json", entity);
        } catch (UnsupportedEncodingException ex) {
            throw new CloudPrintException(ex);
        } finally {
            if (capabilitiesInputStream != null) {
                try {
                    capabilitiesInputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            if (defaultInputStream != null) {
                try {
                    defaultInputStream.close();
                } catch (IOException ex) {
                    throw new CloudPrintException(ex);
                }
            }

            return gson.fromJson(new StringReader(response), RegisterPrinterResponse.class);
        }
    }

    /**
     * <b><a
     * href='https://developers.google.com/cloud-print/docs/proxyinterfaces#list'>/list</a></b><br/>
     * This interface provides a listing of all the printers for the given user.
     * It can be used to compare the printers registered and available locally.
     * If a software connector is connected to multiple printers, this interface
     * is useful to keep the local printers and printers registered with Google
     * Cloud Print in sync. With this interface, the software connector does not
     * need to maintain a state or mapping of the local printers and needs to
     * store only the unique proxy ID required as a parameter.<br/><br/>
     *
     * Response<br/>
     * The response object contains a Boolean success indication and a list of
     * printer objects. If there was a valid software connector specified in the
     * request then only the printers assigned to that software connector are
     * returned. The printer object contains id, name, and proxy fields as shown
     * in the sample output below. Additional details of the printer (for
     * example, capabilities) can be added to the output using the objects
     * parameter described in <a
     * href='https://developers.google.com/cloud-print/docs/proxyinterfaces#commonoutput'>Common
     * Output Control Parameters and Values.</a><br/>
     *
     * @param proxy Identification of the proxy, as submitted while registering
     * the printer.
     * @return ListPrinterResponse
     * @throws CloudPrintException
     */
    public ListPrinterResponse listPrinter(String proxy) throws CloudPrintException {
        String response = openConnection("/list?output=json&proxy=" + proxy);
        return gson.fromJson(new StringReader(response), ListPrinterResponse.class);
    }

    /**
     * For share printer to target email
     *
     * @param printerId Unique printer identification (generated by Google Cloud
     * Print).
     * @param email Google Account or Google Email for share
     * @return SharePrinterResponse
     * @throws CloudPrintException
     */
    public SharePrinterResponse sharePrinter(String printerId, String email) throws CloudPrintException {
        String response = openConnection(new StringBuilder().append("/share?output=json")
                .append("&printerid=").append(printerId)
                .append("&email=").append(email)
                .append("&role=").append(RoleShare.APPENDER)
                .toString());
        
        return gson.fromJson(new StringReader(response), SharePrinterResponse.class);
    }

    /**
     * <b>/ticket</b><br/>
     * Get job ticket from Google Cloud Print<br/><br/>
     * <b>Response</b><br/>
     * XML format : print job detail such as<br/>
     * - document collate<br/>
     * - page output color<br/>
     * - page orientation<br/>
     * - page media size<br/>
     * - duplex page<br/>
     * - copies document<br/>
     * - etc.
     *
     * @param ticketUrl The ticketUrl field in the response points to a job
     * ticket that can be in XPS (<a
     * href='http://en.wikipedia.org/wiki/Open_XML_Paper_Specification'>XML
     * Paper Specification</a>) or PPD (<a
     * href='http://en.wikipedia.org/wiki/PostScript_Printer_Description'>Postscript
     * Printer Description</a>) format. In the future, other job ticket formats
     * may be supported. The fileUrl field in the response points to the data to
     * be printed. As the file download is an HTTP request, the printer /
     * software connector should specify MIME types it can accept to print in
     * the Accept header as defined by HTTP protocol. Google Cloud Print will
     * try to convert the print data to a format acceptable to the printer /
     * software connector.
     *
     * @return
     * @throws CloudPrintException
     */
    public String getJobTicket(String ticketUrl) throws CloudPrintException {
        if (ticketUrl != null) {
            ticketUrl = ticketUrl.replace(CLOUD_PRINT_URL, "");
        }

        return openConnection(ticketUrl);
    }

    private boolean isNotNullAndEmpty(String string) {
        return string != null && string.length() != 0;
    }

    private boolean isNotNullAndIsFile(Object object) {
        return object != null && object instanceof File;
    }
}
