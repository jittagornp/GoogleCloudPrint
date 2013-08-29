/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.example;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import th.co.geniustree.google.cloudprint.api.GoogleCloudPrint;
import th.co.geniustree.google.cloudprint.api.exception.CloudPrintException;
import th.co.geniustree.google.cloudprint.api.model.Job;
import th.co.geniustree.google.cloudprint.api.model.JobListener;
import th.co.geniustree.google.cloudprint.api.model.JobStatus;
import th.co.geniustree.google.cloudprint.api.model.Printer;
import th.co.geniustree.google.cloudprint.api.model.PrinterStatus;
import th.co.geniustree.google.cloudprint.api.model.SubmitJob;
import th.co.geniustree.google.cloudprint.api.model.Ticket;
import th.co.geniustree.google.cloudprint.api.model.response.ControlJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.DeleteJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.DeletePrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.FecthJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.JobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.PrinterInformationResponse;
import th.co.geniustree.google.cloudprint.api.model.response.RegisterPrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SearchPrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SharePrinterResponse;
import th.co.geniustree.google.cloudprint.api.model.response.SubmitJobResponse;
import th.co.geniustree.google.cloudprint.api.model.response.UpdatePrinterResponse;
import th.co.geniustree.google.cloudprint.api.util.PropertiesFileUtils;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class Example {

    private static final Logger LOG = LoggerFactory.getLogger(Example.class);
    private static final GoogleCloudPrint cloudPrint = new GoogleCloudPrint();
    private static Gson gson = new Gson();

    static {
        XMPPConnection.DEBUG_ENABLED = true;
        //System.setProperty("smack.debugEnabled", "true");
    }

    public static void main(String[] args) {
        try {
            Properties properties = PropertiesFileUtils.load("/account.properties");
            String email = properties.getProperty("email");
            String password = properties.getProperty("password");

            cloudPrint.connect(email, password, "geniustree-cloudprint-1.0");
            //searchAllPrinters();
            //searchPrinter("fax", PrinterStatus.ALL);
            //getJobs();
            //getJobOfPrinter("dc6929f5-8fdc-5228-1e73-c9dee3298445");
            //fetchJob("dc6929f5-8fdc-5228-1e73-c9dee3298445");
            jobListener();
            //controlJob("7da91f4a-7faa-2a3d-5cd5-0f2a902a368b", JobStatus.QUEUED, 10, "success.");
            //sharePrinter("dc6929f5-8fdc-5228-1e73-c9dee3298445", "TARGET_EMAIL_TO_SHARE");
            //getPrinterInformation("dc6929f5-8fdc-5228-1e73-c9dee3298445");
            //getPrinterInformation("dc6929f5-8fdc-5228-1e73-c9dee3298445", PrinterStatus.ONLINE);
            //deletePrinter("12280cbe-6486-2c98-c65a-22083bd18b5b");
            //submitJob("810c1d39-981f-cd36-5fdc-951ea5e62613");
            registerPrinter();
            //updatePrinter("126271f6-e5d0-fbce-0574-d0c801612439", "Snagit 11");

        } catch (CloudPrintException ex) {
            LOG.warn(null, ex);
            System.exit(1);
        } catch (IOException ex) {
            LOG.warn(null, ex);
            System.exit(1);
        } finally {
            //cloudPrint.disconnect();
        }
    }

    public static void deleteJob(String jobId) throws CloudPrintException {
        DeleteJobResponse response = cloudPrint.deleteJob(jobId);
        LOG.debug("delete job response => {}", response.isSuccess() + ", " + response.getMessage());
    }

    public static void updatePrinter(String printerId, String newName) throws CloudPrintException {
        Printer printer = new Printer();
        printer.setId(printerId);
        printer.setName(newName);
        printer.setDisplayName(newName);

        UpdatePrinterResponse response = cloudPrint.updatePrinter(printer);
        if (!response.isSuccess()) {
            return;
        }

        LOG.debug("update printer response => {}", response.isSuccess() + ", " + response.getMessage());
    }

    public static void registerPrinter() throws CloudPrintException, IOException {
        InputStream inputStream = null;
        try {
            URL ppdURL = Example.class.getResource("/ppd/ADIST5K.PPD");
            File ppdFile = new File(ppdURL.getPath());
            inputStream = new FileInputStream(ppdFile);

            Printer printer = new Printer();
            printer.setName("Test Printer");
            printer.setDisplayName("Test Printer");
            printer.setProxy("pamarin");
            Set<String> tags = new HashSet<String>();
            tags.add("test");
            tags.add("register");
            printer.setTags(tags);

            String capsHash = DigestUtils.sha512Hex(inputStream);
            printer.setCapsHash(capsHash);
            printer.setStatus("REGISTER");
            printer.setDescription("test register printer");
            printer.setCapabilities(ppdFile);
            printer.setDefaults(ppdFile);

            RegisterPrinterResponse response = cloudPrint.registerPrinter(printer);
            if (!response.isSuccess()) {
                return;
            }

            for (Printer print : response.getPrinters()) {
                LOG.debug("printer response => {}", print);
            }
        } catch (IOException ex) {
            throw ex;
        } catch (CloudPrintException ex) {
            throw ex;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
    }

    public static void submitJob(String printerId) throws Exception {
        InputStream jsonInputStream = null;
        InputStream imageInputStream = null;
        try {
            jsonInputStream = Example.class.getResourceAsStream("/json/submitJobTicketRequest.json");
            imageInputStream = Example.class.getResourceAsStream("/testImage.png");
            byte[] content = IOUtils.toByteArray(imageInputStream);

            String jsonTicket = IOUtils.toString(jsonInputStream);
            Ticket ticket = gson.fromJson(jsonTicket, Ticket.class);

            String json = gson.toJson(ticket);
            LOG.debug("json => {}", json);
            SubmitJob submitJob = new SubmitJob();
            submitJob.setContent(content);
            submitJob.setContentType("image/png");
            submitJob.setPrinterId(printerId);
            submitJob.setTag(Arrays.asList("koalar", "hippo", "cloud"));
            submitJob.setTicket(ticket);
            submitJob.setTitle("testImage.png");
            SubmitJobResponse response = cloudPrint.submitJob(submitJob);
            LOG.debug("submit job response => {}", response.isSuccess() + "," + response.getMessage());
            LOG.debug("submit job id => {}", response.getJob().getId());
            //controlJob(response.getJob().getId(), JobStatus.IN_PROGRESS, 100, "in progress.");
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (jsonInputStream != null) {
                try {
                    jsonInputStream.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }

            if (imageInputStream != null) {
                try {
                    imageInputStream.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
    }

    public static void jobListener() {
        cloudPrint.addJobListener(new JobListener() {
            //
            @Override
            public void onJobArrive(Job job, boolean success, String message) {
                if (success) {
                    try {
                        LOG.debug("job arrive => {}", job);
                        dowloadFile(job);
                        //controlJob(job.getId(), JobStatus.IN_PROGRESS, 100, "in progress.");
                        //do something...
                        String ticketResponse = cloudPrint.getJobTicket(job.getTicketUrl());
                        LOG.debug("ticketResponse => {}", ticketResponse);
                        controlJob(job.getId(), JobStatus.IN_PROGRESS, 100, "progress.");
                    } catch (CloudPrintException ex) {
                        LOG.warn(null, ex);
                    }
                } else {
                    LOG.info("job arrive error message => {}", message);
                }
            }
        });
    }

    public static void fetchJob(String printerId) throws CloudPrintException {
        FecthJobResponse response = cloudPrint.fetchJob(printerId);
        if (!response.isSuccess()) {
            return;
        }

        for (Job job : response.getJobs()) {
            LOG.debug("job response => {}", job);
        }
    }

    public static void getJobOfPrinter(String printerId) throws CloudPrintException {
        JobResponse response = cloudPrint.getJobOfPrinter(printerId);
        if (!response.isSuccess()) {
            return;
        }

        for (Job job : response.getJobs()) {
            LOG.debug("job response => {}", job);
        }
    }

    public static void getJobs() throws CloudPrintException {
        JobResponse response = cloudPrint.getJobs();
        if (!response.isSuccess()) {
            return;
        }

        for (Job job : response.getJobs()) {
            LOG.debug("job response => {}", job);
        }
    }

    public static void getPrinterInformation(String printerId, PrinterStatus status) throws CloudPrintException {
        PrinterInformationResponse response = cloudPrint.getPrinterInformation(printerId, status);
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer information response => {}", printer);
        }
    }

    public static void getPrinterInformation(String printerId) throws CloudPrintException {
        PrinterInformationResponse response = cloudPrint.getPrinterInformation(printerId);
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer information response => {}", printer);
        }
    }

    public static void dowloadFile(Job job) throws CloudPrintException {
        LOG.debug("job => {}", job);
        File directory = new File("C:\\temp\\cloudPrint");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = FilenameUtils.removeExtension(job.getTitle()) + ".pdf";
        File outputFile = new File(directory, fileName);

        cloudPrint.downloadFile(job.getFileUrl(), outputFile);
    }

    public static void controlJob(String jobId, JobStatus status, int code, String message) throws CloudPrintException {
        ControlJobResponse response = cloudPrint.controlJob(jobId, status, code, message);
        LOG.debug("control job response=> {}", response.isSuccess() + ", " + response.getMessage());
    }

    public static void sharePrinter(String printerId, String shareEmail) throws CloudPrintException {
        SharePrinterResponse response = cloudPrint.sharePrinter(printerId, shareEmail);
        LOG.debug("share printer message => {}", response.isSuccess() + ", " + response.getMessage());
    }

    public static void searchPrinter(String query, PrinterStatus status) throws CloudPrintException {
        SearchPrinterResponse response = cloudPrint.searchPrinter(query, status);
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer => {}", printer);
        }
    }

    public static void searchAllPrinters() throws CloudPrintException {
        SearchPrinterResponse response = cloudPrint.searchPrinters();
        if (!response.isSuccess()) {
            return;
        }

        for (Printer printer : response.getPrinters()) {
            LOG.debug("printer => {}", printer);
            getPrinterInformation(printer.getId());
            //sharePrinter(printer.getId(), "TARGET_EMAIL_FOR_SHARE");
        }
    }

    public static void deletePrinter(String printerId) throws CloudPrintException {
        DeletePrinterResponse response = cloudPrint.deletePrinter(printerId);
        LOG.debug("delete printer response => {}", response.isSuccess() + ", " + response.getMessage());
    }
}
