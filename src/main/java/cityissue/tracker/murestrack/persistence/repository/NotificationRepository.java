package cityissue.tracker.murestrack.persistence.repository;

import cityissue.tracker.murestrack.persistence.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}