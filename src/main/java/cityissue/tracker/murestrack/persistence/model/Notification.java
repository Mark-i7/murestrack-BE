package cityissue.tracker.murestrack.persistence.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "`Notification`")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private NotificationType notificationType;

    @Column(name = "text", length = 500)
    @Length(max = 1000)
    private String text;

    @Column(name = "viewed", nullable = false)
    private boolean viewed = false;

    @Column(name = "date_issued")
    @Temporal(TemporalType.DATE)
    private Date dateIssued;

    @ManyToMany(mappedBy = "notifications")
    private Set<User> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification )) return false;
        return id != null && id.equals(((Notification) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
