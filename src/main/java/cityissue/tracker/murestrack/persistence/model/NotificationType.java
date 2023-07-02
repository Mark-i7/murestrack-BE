package cityissue.tracker.murestrack.persistence.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`NotificationType`")
@Data
@ToString(exclude = "notifications")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @Column(name = "name", length = 30)
    @Length(max = 30)
    private String name;

    @OneToMany(
            mappedBy = "notificationType",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Notification> notifications = new ArrayList<>();

    // Utility methods for entity synchronization

    public void addNotification(Notification notification) {
        notifications.add(notification);
        notification.setNotificationType(this);
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
        notification.setNotificationType(null);
    }
}

