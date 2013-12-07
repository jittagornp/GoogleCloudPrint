GoogleCloudPrint
================

Google Cloud Print (GCP) API for Java<br/>

document : <a href="https://developers.google.com/cloud-print/">https://developers.google.com/cloud-print/</a><br/><br/>

<b>About Capabilities</b> : <br/>
There are two ways to do that:<br/>
1) Load printer capabilities and create an XML(XPS format) file using Windows API. Ex.: Using "PrintQueue" class --> GetPrintCapabilitiesAsXml.WriteTo(file)<br/>
2) Obtaining the PPD file of the PostScript printer.<br/>
Uploading one of these files, GCP was able to understand the printer capabilities.<br/><br/>
Recommended from <b>João Vianey</b> (Google Cloud Print developer)<br/>
Thank you very much.

<h3>Example</h3>
<b>Connect to google cloud print</b>
```java
String email = "YOUR_GOOGLE_EMAIL";
String password = "YOUR_GOOGLE_PASSWORD";

GoogleCloudPrint cloudPrint = new GoogleCloudPrint();

//connect to google cloud print
cloudPrint.connect(email, password, "geniustree-cloudprint-1.0");
```
<b>Disconnect from google cloud print</b>
```java
cloudPrint.disconnect();
```
<b>Register printer</a>
```java
// create and setup printer

RegisterPrinterResponse response = cloudPrint.registerPrinter(printer);
```
```java
InputStream inputStream = null;
try {
    //URL ppdURL = Example.class.getResource("/ppd/ADIST5CS.PPD");
    URL xpsURL = Example.class.getResource("/xml/XPSCapabilities.xml");
    
    File capabilitiesFile = new File(xpsURL.getPath());
    inputStream = new FileInputStream(capabilitiesFile);


    /*
    * require
    * - printer name
    * - display name
    * - proxy
    * - capabilities 
    * - defaults
    * - tag
    * - status
    * - description
    * - capsHash
    */

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
    printer.setCapabilities(capabilitiesFile);
    printer.setDefaults(capabilitiesFile);

    RegisterPrinterResponse response = cloudPrint.registerPrinter(printer);
    if (!response.isSuccess()) {
        LOG.debug("message = > {}", response.getMessage());
        return;
    }
    
    for (Printer print : response.getPrinters()) {
        LOG.debug("printer response => {}", print);
    }
} catch (IOException ex) {
    LOG.warn(null, ex);
} catch (CloudPrintException ex) {
    LOG.warn(null, ex);
} finally {
    if (inputStream != null) {
        try {
            inputStream.close();
        } catch (IOException ex) {
            LOG.warn(null, ex);
        }
    }
}
```
<b>Job listener</b> (listener job from google talk notification)
```java
cloudPrint.addJobListener(new JobListener() {
    //
    @Override
    public void onJobArrive(Job job, boolean success, String message) {
    
        //do something ...
    
    }
});    
```
```java
cloudPrint.addJobListener(new JobListener() {
    //
    @Override
    public void onJobArrive(Job job, boolean success, String message) {
        if (success) {
            try {
                LOG.debug("job arrive => {}", job);
                File directory = new File("C:\\temp\\cloudPrint");
                if (!directory.exists()) {
                  directory.mkdirs();
                }

                //download print file from google cloud print     
                String fileName = FilenameUtils.removeExtension(job.getTitle()) + ".pdf";
                File outputFile = new File(directory, fileName);
                 
                cloudPrint.downloadFile(job.getFileUrl(), outputFile);
        
 
 
                //do something...
                
                
                //get job information or printing option such as
                // - document collate 
                // - page output color 
                // - page orientation 
                // - page media size 
                // - duplex page 
                // - copies document
                // ...
                // response is XML format
                String ticketResponse = cloudPrint.getJobTicket(job.getTicketUrl());
                LOG.debug("ticketResponse => {}", ticketResponse);



                //update job status
                ControlJobResponse response = cloudPrint.controlJob(job.getId(), JobStatus.DONE, 200, "success.");
                LOG.debug("control job response=> {}", response.isSuccess() + ", " + response.getMessage());
            } catch (CloudPrintException ex) {
                LOG.warn(null, ex);
            }
        } else {
            LOG.info("job arrive error message => {}", message);
        }
    }
});
```
<b>Submit job</b> (send job to google cloud print)
```java
//create and setup job

SubmitJobResponse response = cloudPrint.submitJob(submitJob);
```
```java
//send job to target printer
//printer id = "810c1d39-981f-cd36-5fdc-951ea5e62613"

InputStream jsonInputStream = null;
InputStream imageInputStream = null;
try {
    jsonInputStream = Example.class.getResourceAsStream("/json/submitJobTicketRequest.json");
    imageInputStream = Example.class.getResourceAsStream("/testImage.png");
    byte[] content = IOUtils.toByteArray(imageInputStream);

    //get job option from json file
    //such as  
    // - document collate 
    // - page output color 
    // - page orientation 
    // - page media size 
    // - duplex page 
    // - copies document
    // ...
    String jsonTicket = IOUtils.toString(jsonInputStream);
    Ticket ticket = gson.fromJson(jsonTicket, Ticket.class);

    String json = gson.toJson(ticket);
    LOG.debug("json => {}", json);
    
    //create job
    SubmitJob submitJob = new SubmitJob();
    submitJob.setContent(content);
    submitJob.setContentType("image/png");
    submitJob.setPrinterId("810c1d39-981f-cd36-5fdc-951ea5e62613"); //*****
    submitJob.setTag(Arrays.asList("koalar", "hippo", "cloud"));
    submitJob.setTicket(ticket);
    submitJob.setTitle("testImage.png");

    //send job
    SubmitJobResponse response = cloudPrint.submitJob(submitJob);
    LOG.debug("submit job response => {}", response.isSuccess() + "," + response.getMessage());
    LOG.debug("submit job id => {}", response.getJob().getId());

    //control job (update job)
    //...
    //...
} catch (Exception ex) {
    LOG.warn(null, ex);
} finally {
    if (jsonInputStream != null) {
        try {
            jsonInputStream.close();
        } catch (IOException ex) {
            LOG.warn(null, ex);
        }
    }

    if (imageInputStream != null) {
        try {
            imageInputStream.close();
        } catch (IOException ex) {
            LOG.warn(null, ex);
        }
    }
}
```
<b>Delete job</b>
```java
//jobId generate by google cloud print

DeleteJobResponse response = cloudPrint.deleteJob(jobId);
LOG.debug("delete job response => {}", response.isSuccess() + ", " + response.getMessage());
```
<b>Update printer informaton</b>
```java
//printer id = "a1dbe503-eb96-6d26-dc7b-a290a1cfaf3b"

Printer printer = new Printer();
printer.setId("a1dbe503-eb96-6d26-dc7b-a290a1cfaf3b");
printer.setName("Adobe PDF2"); //set new name
printer.setDisplayName("Adobe PDF2");

UpdatePrinterResponse response = cloudPrint.updatePrinter(printer);
LOG.debug("update printer response => {}", response.isSuccess() + ", " + response.getMessage());
```
<b>Search printer</b>
```java
//search all printers which name is "fax"
//PrinterStatus.ALL is all printer status 
// - ONLINE
// - UNKNOWN
// - OFFLINE
// - DORMANT

SearchPrinterResponse response = cloudPrint.searchPrinter("fax", PrinterStatus.ALL);
if (!response.isSuccess()) {
    LOG.debug("message = > {}", response.getMessage());
    return;
}

for (Printer printer : response.getPrinters()) {
    LOG.debug("printer => {}", printer);
}
```
<b>Get printer information</a>
```java
//printer id = "dc6929f5-8fdc-5228-1e73-c9dee3298445"

PrinterInformationResponse response = cloudPrint.getPrinterInformation("dc6929f5-8fdc-5228-1e73-c9dee3298445");
if (!response.isSuccess()) {
    LOG.debug("message = > {}", response.getMessage());
    return;
}

for (Printer printer : response.getPrinters()) {
    LOG.debug("printer information response => {}", printer);
}
```
<b>Update job status</b> (Control  job)
```java
//parameter
//job id : get from subscribe job (generate by google cloud print)
//job status : 
// - QUEUED
// - IN_PROGRESS
// - DONE
// - ERROR
//job code : get from printer
//job message : get from printer

ControlJobResponse response = cloudPrint.controlJob(job.getId(), JobStatus.IN_PROGRESS, 100, "PROGRESSING.");
LOG.debug("control job response=> {}", response.isSuccess() + ", " + response.getMessage());
```
<b>Get job</b> (print job)<br/>
get all jobs
```java
JobResponse response = cloudPrint.getJobs();
if (!response.isSuccess()) {
    LOG.debug("message = > {}", response.getMessage());
    return;
}

for (Job job : response.getJobs()) {
    LOG.debug("job response => {}", job);
}
```
get job of printer
```java
//printer id = "dc6929f5-8fdc-5228-1e73-c9dee3298445"

JobResponse response = cloudPrint.getJobOfPrinter("dc6929f5-8fdc-5228-1e73-c9dee3298445");
if (!response.isSuccess()) {
    LOG.debug("message = > {}", response.getMessage());
    return;
}

for (Job job : response.getJobs()) {
    LOG.debug("job response => {}", job);
}
```
<b>Delete printer</b>
```java
//printer id = "12280cbe-6486-2c98-c65a-22083bd18b5b"

DeletePrinterResponse response = cloudPrint.deletePrinter("12280cbe-6486-2c98-c65a-22083bd18b5b");
LOG.debug("delete printer response => {}", response.isSuccess() + ", " + response.getMessage());
```
<b>Share printer</b>
```java
//printer id = "dc6929f5-8fdc-5228-1e73-c9dee3298445"
//target email = "jittagorn@geniustree.co.th"

SharePrinterResponse response = cloudPrint.sharePrinter("dc6929f5-8fdc-5228-1e73-c9dee3298445", "jittagorn@geniustree.co.th");
LOG.debug("share printer message => {}", response.isSuccess() + ", " + response.getMessage());
```
develop by <b>jittagorn pitakmetagoon</b><br/>
java developer Thailand<br/>
work at Geniustree Co., Ltd<br/>
blog : <a href="http://na5cent.blogspot.com">na5cent.blogspot.com</a><br/>
email : jittagorn@geniustree.co.th
