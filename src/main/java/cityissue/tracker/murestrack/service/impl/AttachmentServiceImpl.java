package cityissue.tracker.murestrack.service.impl;

import cityissue.tracker.murestrack.service.AttachmentService;
import cityissue.tracker.murestrack.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.StringUtils;
import cityissue.tracker.murestrack.persistence.model.Attachment;
import cityissue.tracker.murestrack.persistence.repository.AttachmentRepository;
import cityissue.tracker.murestrack.persistence.repository.ReportRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Service
public class AttachmentServiceImpl implements AttachmentService {


    private AttachmentRepository attachmentRepository;
    private ReportRepository reportRepository;

    private ReportService reportService;

    @Autowired
    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, ReportRepository reportRepository, ReportService reportService) {
        this.attachmentRepository = attachmentRepository;
        this.reportRepository = reportRepository;
        this.reportService = reportService;
    }

    public Long saveFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Attachment FileDB = null;
        try {
            FileDB = new Attachment(fileName, file.getContentType(), file.getBytes());
        } catch (IOException e) {
            return null;
        }
        return attachmentRepository.save(FileDB).getId();
    }
    public Attachment getById(Long id) {
        return attachmentRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id){
        attachmentRepository.deleteById(id);
    }
}

