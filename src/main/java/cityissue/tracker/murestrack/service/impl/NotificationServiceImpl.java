package cityissue.tracker.murestrack.service.impl;

import cityissue.tracker.murestrack.dto.NotificationDTO;
import cityissue.tracker.murestrack.persistence.model.*;
import cityissue.tracker.murestrack.persistence.repository.NotificationRepository;
import cityissue.tracker.murestrack.persistence.repository.NotificationTypeRepository;
import cityissue.tracker.murestrack.persistence.repository.UserRepository;
import cityissue.tracker.murestrack.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTypeRepository notificationTypeRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    @Autowired
    public NotificationServiceImpl(
            NotificationTypeRepository notificationTypeRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository, WebSocketService webSocketService) {
        this.notificationTypeRepository = notificationTypeRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
    }

    @Override
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    @Transactional
    void sendNotification(
            String notificationType,
            String text,
            List<String> usernames
    ) {
        Optional<NotificationType> notificationTypeOptional = notificationTypeRepository.findByName(notificationType);
        if (notificationTypeOptional.isEmpty()) {
            return;
        }
        NotificationType notificationTyp = notificationTypeOptional.get();
        Notification newNotification = new Notification();
        newNotification.setText(text);
        newNotification.setDateIssued(new Date());
        notificationTyp.addNotification(newNotification);
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .notificationType(notificationType)
                .text(text)
                .dateIssued(new Date())
                .build();
        usernames.forEach(
                username -> userRepository.findByUsername(username)
                        .ifPresent(user -> {
                            user.addNotification(newNotification);
                            webSocketService.sendMessage(notificationDTO, username);
                        })
        );
        notificationRepository.save(newNotification);
        notificationTypeRepository.save(notificationTyp);
    }

    @Override
    public void sendWelcomeNewUser(User newUser) {
        String text = String.format("Hello %s!\n Your personal information is as follows: \n " +
                        "First Name -> %s\n" +
                        "Last Name -> %s\n" +
                        "Email address -> %s\n" +
                        "Phone Number -> %s\n",
                newUser.getUsername(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getEmail(),
                newUser.getPhoneNumber());

        this.sendNotification("WELCOME_NEW_USER", text, Collections.singletonList(newUser.getUsername()));
    }

    @Override
    public void sendUserUpdated(
            User old,
            User updated,
            String executor
    ) {
        String text = String.format("User %s was updated!\n His/Her personal information is as follows: \n " +
                        "First Name -> %s (updated), %s (old)\n" +
                        "Last Name -> %s (updated), %s (old)\n" +
                        "Email address -> %s (updated), %s (old)\n" +
                        "Phone Number -> %s (updated), %s (old)\n",
                updated.getUsername(),
                updated.getFirstName(), old.getFirstName(),
                updated.getLastName(), old.getLastName(),
                updated.getEmail(), old.getEmail(),
                updated.getPhoneNumber(), old.getPhoneNumber());

//        this.sendNotification("USER_UPDATED", text, Arrays.asList(executor, updated.getUsername()));
        this.sendNotification("USER_UPDATED", text, Collections.singletonList(executor));
        this.sendNotification("USER_UPDATED", text, Collections.singletonList(updated.getUsername()));

    }

    @Override
    public void sendUserDeleted(User user) {
        String text = String.format("User %s was deleted! His/Her personal information is as follows: \n " +
                        "First Name -> %s\n" +
                        "Last Name -> %s\n" +
                        "Email address -> %s\n" +
                        "Phone Number -> %s\n",
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber());

        List<User> haveUserManagementPermission = userRepository.findAll()
                .stream()
                .filter(userEntity -> userEntity.getRoles()
                        .stream()
                        .anyMatch(role -> role.getPermissions()
                                .stream()
                                .anyMatch(
                                        permission -> permission.getTitle().equals("USER_MANAGEMENT")
                                )
                        )
                ).collect(Collectors.toList());

//        List<String> listUsernamesToSendEmail = haveUserManagementPermission.stream()
//                .map(User::getUsername)
//                .collect(Collectors.toList());
//        this.sendNotification("USER_DELETED", text, listUsernamesToSendEmail);

        haveUserManagementPermission.forEach(
                (adresee) -> this.sendNotification("USER_DELETED", text, Collections.singletonList(adresee.getUsername())));
    }

    private static String formatReportData(Report report) {
        return String.format("Report data: \n " +
                        "Title -> %s\n" +
                        "Description -> %s\n" +
                        "Category -> %s\n" +
                        "Location -> %s\n" +
                        "Target date -> %s\n" +
                        "Severity -> %s\n" +
                        "Creator -> %s\n" +
                        "Assignee -> %s\n" +
                        "Status -> %s\n" +
                        "Attachment -> %s\n",
                report.getTitle(),
                report.getDescription(),
                report.getCategory(),
                report.getLocation(),
                report.getTargetDate() == null ? null : report.getTargetDate().toString(),
                report.getSeverity(),
                report.getCreator().getUsername(),
                report.getAssignee().getUsername(),
                report.getStatus(),
                "");
        //report.getAttachment().getFileName());
    }

    @Override
    public void sendReportUpdated(Report report) {
        String text = "";
        if (report.getStatus().equals(Status.NEW)) {
            text += "New report added! \n";
        } else {
            text += "A report was just updated! \n";
        }
        text += formatReportData(report);

        String creator = report.getCreator().getUsername();
        String assignee = report.getAssignee().getUsername();
//        this.sendNotification("REPORT_UPDATED", text, Arrays.asList(creator, assignee));
        this.sendNotification("REPORT_UPDATED", text, Collections.singletonList(creator));
        this.sendNotification("REPORT_UPDATED", text, Collections.singletonList(assignee));
    }

    @Override
    public void sendReportClosed(Report report) {
        String text = "A report was just closed! \n" + formatReportData(report);
        String creator = report.getCreator().getUsername();
        String assignee = report.getAssignee().getUsername();
//        this.sendNotification("REPORT_CLOSED", text, Arrays.asList(creator, assignee));
        this.sendNotification("REPORT_CLOSED", text, Collections.singletonList(creator));
        this.sendNotification("REPORT_CLOSED", text, Collections.singletonList(assignee));
    }

    @Override
    public void sendReportStatusUpdated(Report report, String oldStatus) {
        String text = String.format("Report just changed status from %s to %s! ",
                oldStatus,
                report.getStatus()) + formatReportData(report);
        String creator = report.getCreator().getUsername();
        String assignee = report.getAssignee().getUsername();
//        this.sendNotification("REPORT_STATUS_CHANGED", text, Arrays.asList(creator, assignee));
        this.sendNotification("REPORT_STATUS_CHANGED", text, Collections.singletonList(creator));
        this.sendNotification("REPORT_STATUS_CHANGED", text, Collections.singletonList(assignee));
    }

    @Override
    public void sendUserDeactivated(User user) {
        String text = String.format("User %s\n was deactivated! His/Her personal information is as follows: \n " +
                        "First Name -> %s\n" +
                        "Last Name -> %s\n" +
                        "Email address -> %s\n" +
                        "Phone Number -> %s\n",
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber());

        List<User> haveADM_Role = userRepository.findAll()
                .stream()
                .filter(u -> u.getRoles()
                        .stream()
                        .anyMatch(
                                role -> role.getTitle().equals("ADM"))
                )
                .collect(Collectors.toList());

        List<String> adminUsernames = haveADM_Role.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

//        this.sendNotification("USER_DEACTIVATED", text, adminUsernames);
        adminUsernames.forEach(
                (adresee) -> this.sendNotification("USER_DEACTIVATED", text, Collections.singletonList(adresee)));
    }

    @Override
    @Transactional
    public void markUserNotificationsViewed(String username) {
        userRepository.findByUsername(username)
                .ifPresent(
                        user -> user.getNotifications()
                                .forEach(notification -> {
                                    notification.setViewed(true);
                                    notificationRepository.save(notification);
                                })
                );
    }
}
