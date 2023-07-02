package cityissue.tracker.murestrack.service;

import cityissue.tracker.murestrack.persistence.model.Permission;
import cityissue.tracker.murestrack.persistence.model.UserRole;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface UserRoleService {
    UserRole getRoleByTitle(String title);
    boolean setRolePermissions(String role, Set<Permission> permissions);
    List<UserRole> getAll();
    List<Permission> getAllPermissions();
    Permission getPermissionByTitle(String perm);
}
