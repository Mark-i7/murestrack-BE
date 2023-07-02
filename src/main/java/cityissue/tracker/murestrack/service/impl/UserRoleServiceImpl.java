package cityissue.tracker.murestrack.service.impl;


import cityissue.tracker.murestrack.persistence.model.Permission;
import cityissue.tracker.murestrack.persistence.model.UserRole;
import cityissue.tracker.murestrack.persistence.repository.PermissionRepository;
import cityissue.tracker.murestrack.persistence.repository.UserRoleRepository;
import cityissue.tracker.murestrack.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private UserRoleRepository userRoleRepository;
    private PermissionRepository permissionRepository;



    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, PermissionRepository permissionRepository) {
        this.userRoleRepository = userRoleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public UserRole getRoleByTitle(String title) {
        return userRoleRepository.findByTitle(title).orElse(null);
    }

    @Override
    @Transactional
    public boolean setRolePermissions(String role, Set<Permission> permissions) {
        Optional<UserRole> maybeUserRole = userRoleRepository.findByTitle(role);
        if(maybeUserRole.isEmpty()){
            return false;
        }
        UserRole userRole = maybeUserRole.get();
        userRole.removePermissions(userRole.getPermissions());
        userRole.addPermissions(permissions);
        return true;
    }

    @Override
    public List<UserRole> getAll() {
        return userRoleRepository.findAll();
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission getPermissionByTitle(String perm) {
        return permissionRepository.findByTitle(perm).orElse(null);
    }

}
