package cityissue.tracker.murestrack.controller;

import cityissue.tracker.murestrack.dto.NotificationDTO;
import cityissue.tracker.murestrack.persistence.model.Notification;
import cityissue.tracker.murestrack.persistence.model.User;
import cityissue.tracker.murestrack.service.impl.NotificationServiceImpl;
import cityissue.tracker.murestrack.service.impl.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class NotificationController {

    NotificationServiceImpl notificationService;
    UserServiceImpl userService;
    ModelMapper modelMapper;

    @Autowired
    public NotificationController(NotificationServiceImpl notificationService, UserServiceImpl userService, ModelMapper modelMapper) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/notifications/{username}")
    List<NotificationDTO> getAllNotifications(@PathVariable String username) {
        User user = userService.getByUsername(username);

        return user.getNotifications()
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .sorted(Comparator.comparing(NotificationDTO::getId).reversed())
                .collect(Collectors.toList());
    }

    @PostMapping("/notifications/{username}")
    void markAsViewed(@PathVariable String username) {
        notificationService.markUserNotificationsViewed(username);
    }
}
