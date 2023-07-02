package cityissue.tracker.murestrack.service;

import cityissue.tracker.murestrack.persistence.model.Report;
import cityissue.tracker.murestrack.persistence.model.Notification;
import cityissue.tracker.murestrack.persistence.model.User;

import java.util.List;

public interface NotificationService {

    List<Notification> getAll();
    void sendWelcomeNewUser(User newUser);
    void sendUserUpdated(User old, User updated, String executor);
    void sendUserDeleted(User user);
    void sendReportUpdated(Report report);
    void sendReportClosed(Report report);
    void sendReportStatusUpdated(Report report, String oldStatus);
    void sendUserDeactivated(User user);

    void markUserNotificationsViewed(String username);
}