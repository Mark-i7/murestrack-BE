package cityissue.tracker.murestrack.service.impl;

import cityissue.tracker.murestrack.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(NotificationDTO notificationDTO, String username) {
        messagingTemplate.convertAndSend("/topic/notification/" + username, notificationDTO);
    }
}