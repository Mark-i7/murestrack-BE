package cityissue.tracker.murestrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String notificationType;
    private String text;
    private boolean viewed;
    private Date dateIssued;
}
