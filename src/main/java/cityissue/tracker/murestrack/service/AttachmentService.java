package cityissue.tracker.murestrack.service;

import cityissue.tracker.murestrack.persistence.model.Attachment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface AttachmentService {
    Long saveFile(MultipartFile file) throws IOException;
    Attachment getById(Long id);

    void deleteById(Long id);

}