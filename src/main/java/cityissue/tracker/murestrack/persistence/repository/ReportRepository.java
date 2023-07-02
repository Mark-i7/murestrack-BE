package cityissue.tracker.murestrack.persistence.repository;

import cityissue.tracker.murestrack.persistence.model.Category;
import cityissue.tracker.murestrack.persistence.model.Report;
import cityissue.tracker.murestrack.persistence.model.Severity;
import cityissue.tracker.murestrack.persistence.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByTitle(String title);
    @Query("SELECT COUNT(r) FROM Report r WHERE r.category = :category")
    int countByCategory(Category category);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.severity = :severity")
    int countBySeverity(Severity severity);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    int countByStatus(Status status);
}
