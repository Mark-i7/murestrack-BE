package cityissue.tracker.murestrack.persistence.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "`User`")
@Data
@Builder(toBuilder = true)
@ToString(exclude = {"notifications", "roles", "createdReports", "assignedReports"})
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "ID")
    private Long id;

    @Column(name = "first_name", length = 30, nullable = false)
    @Length(max = 30, min = 1)
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    @Length(max = 30, min = 1)
    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Column(name = "phone_number", length = 30, nullable = false)
    @Length(max = 30)
    @NotBlank(message = "Phone number is mandatory")
//  @Pattern(regexp="^\\+4(0[0-9]{9}|9[0-9]{6,13})$")
    private String phoneNumber;

    @Column(name = "email", length = 30, nullable = false)
    @Length(max = 30)
    @NotBlank(message = "Email is mandatory")
    @Email
//  @Pattern(regexp="^\\p{Alnum}+@msggroup\\.com$")
    private String email;

    @Column(name = "username", length = 15, unique = true)
    @Length(max = 15)
    private String username;

    @Column(name = "password", length = 72)
    @Length(max = 72)
    private String password;

    private UserStatus active = UserStatus.ACTIVE;

    // One-To-Many associations

    @OneToMany(
            mappedBy = "creator",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Report> createdReports = new ArrayList<>();

    @OneToMany(
            mappedBy = "assignee",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Report> assignedReports = new ArrayList<>();

    // Many-To-Many associations

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "UserRoleBridge",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles = new HashSet<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "UserNotificationBridge",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "notification_id")
    )
    private Set<Notification> notifications = new HashSet<>();

    // Utility methods for entity synchronization

    public void addNotification(Notification notification) {
        notifications.add(notification);
        notification.getUsers().add(this);
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
        notification.getUsers().remove(this);
    }

    public void addCreatedReport(Report report) {
        createdReports.add(report);
        report.setCreator(this);
    }

    public void removeCreatedReport(Report report) {
        createdReports.remove(report);
        report.setCreator(null);
    }

    public void addAssignedReport(Report report) {
        assignedReports.add(report);
        report.setAssignee(this);
    }

    public void removeAssignedReport(Report report) {
        assignedReports.remove(report);
        report.setAssignee(null);
    }

    public void addRole(UserRole role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(UserRole role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }

    public void removeRoles(Set<UserRole> roles) {
        this.roles.removeAll(roles);
        roles.forEach(role->role.getUsers().remove(this));
    }

    public void addRoles(Set<UserRole> roles) {
        this.roles.addAll(roles);
        roles.forEach(role->role.getUsers().add(this));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
