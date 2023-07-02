package cityissue.tracker.murestrack.persistence.repository;

import cityissue.tracker.murestrack.persistence.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>{
    Optional<UserRole> findByTitle(String title);

}