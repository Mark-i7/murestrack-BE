package cityissue.tracker.murestrack.persistence.repository;

import cityissue.tracker.murestrack.persistence.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,Long> {
    Optional<Attachment> findByFileName(String filename);
//    List<AttachmentDto> findAllByReport_Id(Long id);
}