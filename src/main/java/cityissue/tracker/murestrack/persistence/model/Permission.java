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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "`Permission`")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @Column(name = "title", length = 30)
    @Length(max = 30)
    private String title;

    @Column(name = "description", length = 250)
    @Length(max = 250)
    private String description;

    @ManyToMany(mappedBy = "permissions")
    private Set<UserRole> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        return id != null && id.equals(((Permission) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}

