package cityissue.tracker.murestrack.controller;

import cityissue.tracker.murestrack.persistence.model.CustomResponseEntity;
import cityissue.tracker.murestrack.persistence.model.Permission;
import cityissue.tracker.murestrack.persistence.model.UserRole;
import cityissue.tracker.murestrack.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation .*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PermissionController {
    private UserRoleService userRoleService;

    @Autowired
    public PermissionController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping("/authorization/roles")
    List<String> getUserRoles() {
        return userRoleService.getAll()
                .stream()
                .map(UserRole::getTitle)
                .collect(Collectors.toList());
    }

    @GetMapping("/authorization/permissions")
    List<String> getPermissions() {
        return userRoleService.getAllPermissions()
                .stream()
                .map(Permission::getTitle)
                .collect(Collectors.toList());
    }

    @GetMapping("/authorization/permissions/{role}")
    public List<String> getPermissionsByRoleTitle(@PathVariable String role) {
        return userRoleService.getRoleByTitle(role)
                .getPermissions()
                .stream()
                .map(Permission::getTitle)
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/authorization/permissions/{role}")
    public ResponseEntity<Object> setRolePermissions(@PathVariable String role, @RequestBody Map<String, List<String>> grants) {
        Set<Permission> permissions = grants.get("permissions").stream().map(perm->userRoleService.getPermissionByTitle(perm)).collect(Collectors.toSet());
        if (userRoleService.setRolePermissions(role, permissions)) {
            return new ResponseEntity<>(CustomResponseEntity.getMessage("Permissions granted successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to grant the permissions."), HttpStatus.OK);
    }
}
