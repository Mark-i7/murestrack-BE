package cityissue.tracker.murestrack.controller;

import cityissue.tracker.murestrack.dto.AttachmentDTO;
import cityissue.tracker.murestrack.dto.ReportDto;
import cityissue.tracker.murestrack.persistence.model.*;
import cityissue.tracker.murestrack.service.AttachmentService;
import cityissue.tracker.murestrack.service.ReportService;
import cityissue.tracker.murestrack.service.NotificationService;
import cityissue.tracker.murestrack.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ReportController {
    private ReportService reportService;

    private AttachmentService attachmentService;
    private NotificationService notificationService;
    private UserService userService;

    @Autowired
    public ReportController(ReportService reportService, AttachmentService attachmentService, NotificationService notificationService, UserService userService) {
        this.reportService = reportService;
        this.attachmentService = attachmentService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/reports")
    List<ReportDto> getReports(){
        return reportService.getAll()
                .stream()
                .map(ReportDto::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/reports/counts")
    public ReportCounts getReportCounts() {
        return reportService.getReportCounts();
    }
    @PostMapping("/reports/add")
    ResponseEntity<Object> addReport(@RequestBody ReportDto report){
        User creator = userService.getByUsername(report.getCreator());
        User assignee = userService.getByUsername(report.getAssignee());
        AttachmentDTO attachmentDTO = report.getAttachmentDTO();
        Attachment attachment = null;
        if(attachmentDTO != null){
            attachment = attachmentService.getById(attachmentDTO.getId());
        }
        Report newReport = Report.builder()
                .title(report.getTitle())
                .description(report.getDescription())
                .category(report.getCategory())
                .location(report.getLocation())
                .targetDate(report.getTargetDate())
                .severity(report.getSeverity())
                .status(Status.NEW)
                .creator(creator)
                .assignee(assignee)
                .attachment(attachment)
                .build();
        boolean result = reportService.addReport(newReport);
        if(!result){
            return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to add a new Report."), HttpStatus.OK);

        }
        notificationService.sendReportUpdated(newReport);
        return new ResponseEntity<>(CustomResponseEntity.getMessage("New Report added successfully!"), HttpStatus.OK);
    }

    @PatchMapping("/reports/close/{id}")
    ResponseEntity<Object> closeReport(@PathVariable Long id){
        Report closedReport = reportService.getReportById(id);
        if(closedReport == null){
            return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to close the Report"), HttpStatus.OK);
        }
        Status oldReportStatus = closedReport.getStatus();
        if((oldReportStatus.equals(Status.FIXED)) || (oldReportStatus.equals(Status.REJECTED))){
            closedReport = reportService.closeReport(id);
            closedReport.setStatus(Status.CLOSED);
        }else{
            return new ResponseEntity<>(CustomResponseEntity.getError("Cannot close the Report!"),HttpStatus.OK);
        }
        notificationService.sendReportClosed(closedReport);
        return new ResponseEntity<>(CustomResponseEntity.getMessage("Report successfully closed!"), HttpStatus.OK);
    }

    @PutMapping("/reports/update/{id}")
    ResponseEntity<Object> updateReport(@PathVariable Long id, @RequestBody ReportDto report){
        User assignee = userService.getByUsername(report.getAssignee());
        User creator = userService.getByUsername(report.getCreator());
        AttachmentDTO attachmentDTO = report.getAttachmentDTO();
        Attachment attachment = null;
        if(attachmentDTO != null){
            attachment = attachmentService.getById(attachmentDTO.getId());
        }
        Report updatedReport = Report.builder()
                .title(report.getTitle())
                .description(report.getDescription())
                .category(report.getCategory())
                .location(report.getLocation())
                .severity(report.getSeverity())
                .status(report.getStatus())
                .targetDate(report.getTargetDate())
                .creator(creator)
                .assignee(assignee)
                .attachment(attachment)
                .build();
        Report oldReport = reportService.editReport(id, updatedReport);
        if(oldReport == null){
            return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to edit the Report"), HttpStatus.OK);
        }
        if(oldReport.getAttachment() != updatedReport.getAttachment()){
            if(oldReport.getAttachment() != null){
                this.attachmentService.deleteById(oldReport.getAttachment().getId());
            }
        }
        notificationService.sendReportUpdated(updatedReport);
        return new ResponseEntity<>(CustomResponseEntity.getMessage("Report successfully edited!"), HttpStatus.OK);
    }

    @GetMapping("/reports/{id}")
    public ReportDto getById(@PathVariable String id) {
        return ReportDto.convertToDTO(this.reportService.getReportById(Long.parseLong(id)));
    }

    @PostMapping(value = "/reports/export/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void exportToPdf(@PathVariable String id, HttpServletResponse response) {
        String filename = this.reportService.exportReportToPdf(Long.parseLong(id));
        File pdfFile = new File("./src/main/resources/generated/" + filename);
        try {
            InputStream targetStream = new FileInputStream(pdfFile);
            response.addHeader("Content-disposition", "attachment;filename=" + filename);
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            IOUtils.copy(targetStream, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
