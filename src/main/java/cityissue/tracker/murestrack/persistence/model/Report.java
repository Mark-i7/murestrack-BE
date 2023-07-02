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

@Entity
@Table(name = "`Report`")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @Column(name = "title", length = 30, nullable = false)
    @Length(max = 30)
    private String title;

    @Column(name = "description", length = 500, nullable = false)
    @Length(min = 250, max = 500)
    private String description;

    @Column(name = "category")
    private Category category;

    @Column(name = "location", length = 30)
    @Length(max = 30)
    private String location;

    @Column(name = "target_date")
    private Date targetDate;

    @Column(name = "severity")
    private Severity severity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name="status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @OneToOne(fetch = FetchType.LAZY)
    private Attachment attachment;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        return id != null && id.equals(((Report) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
