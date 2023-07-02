package cityissue.tracker.murestrack.controller;

import cityissue.tracker.murestrack.persistence.model.Attachment;
import cityissue.tracker.murestrack.persistence.repository.AttachmentRepository;
import cityissue.tracker.murestrack.service.AttachmentService;
import cityissue.tracker.murestrack.service.ReportService;
import cityissue.tracker.murestrack.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AttachmentController {
    private AttachmentRepository attachmentRepository;

    private AttachmentService attachmentService;

    private ReportService reportService;

    @Autowired
    public AttachmentController(AttachmentRepository attachmentRepository, AttachmentService attachmentService,ReportService reportService) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
        this.reportService = reportService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> upload(@RequestParam("file") MultipartFile file) {
        String message = "";
        try{
            Long id = attachmentService.saveFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(id + ""));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }

    }


    @DeleteMapping(path = {"/attachments/delete"})
    void delete(@RequestParam Long id) {
        attachmentService.deleteById(id);
    }


    @GetMapping(path = {"/reports/{id}/attachment"})
    public ResponseEntity<byte[]>  getReportAttachment(@PathVariable("id") Long id)  {
        Attachment attachment = attachmentService.getById(reportService.getReportById(id).getAttachment().getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(attachment.getData());
    }


}
