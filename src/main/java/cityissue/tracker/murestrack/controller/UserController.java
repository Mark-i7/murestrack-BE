package cityissue.tracker.murestrack.controller;

import cityissue.tracker.murestrack.dto.UserDTO;
import cityissue.tracker.murestrack.persistence.model.CustomResponseEntity;
import cityissue.tracker.murestrack.persistence.model.Permission;
import cityissue.tracker.murestrack.persistence.model.User;
import cityissue.tracker.murestrack.persistence.model.UserRole;
import cityissue.tracker.murestrack.service.NotificationService;
import cityissue.tracker.murestrack.service.UserRoleService;
import cityissue.tracker.murestrack.service.UserService;
import cityissue.tracker.murestrack.service.impl.EmailService;
import io.jsonwebtoken.Jwts;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private ModelMapper modelMapper;
    private UserService userService;
    private EmailService emailService;
    private UserRoleService userRoleService;
    private NotificationService notificationService;
    private PasswordEncoder passwordEncoder;



    @Autowired
    public UserController(ModelMapper modelMapper, UserService userService, UserRoleService userRoleService, NotificationService notificationService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @GetMapping("/users")
    List<UserDTO> getUsers() {
        return userService.getAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/users/{uname}/permissions")
    List<String> getUserPermissions(@PathVariable String uname) {
        return userService.getUserPermissions(uname).stream()
                .map(Permission::getTitle)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{uname}")
    UserDTO getUserByUsername(@PathVariable String uname) {
        User user = userService.getByUsername(uname);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setAuthorizations(user.getRoles().stream().map(UserRole::getTitle).collect(Collectors.toList()));
        return userDTO;
    }


    @PostMapping("/users/add")
    ResponseEntity<Object> addUser(@RequestBody UserDTO user) {
        User newUser = modelMapper.map(user, User.class);
        String password = UserService.generateCommonLangPassword();
        System.out.println(password);
        newUser.setPassword(passwordEncoder.encode(password));
        user.getAuthorizations().stream().map(userRoleService::getRoleByTitle).forEach(newUser::addRole);
        boolean result = userService.addUser(newUser);
        if (!result) {
            return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to add a new user."), HttpStatus.OK);
        }
        notificationService.sendWelcomeNewUser(newUser);
        emailService.sendWelcomeNewUser(newUser.getUsername(), password);
        return new ResponseEntity<>(CustomResponseEntity.getMessage("New user successfully added!"), HttpStatus.OK);
    }

    @DeleteMapping("/users/delete/{uname}")
    ResponseEntity<Object> deleteUser(@PathVariable String uname) {
        User deletedUser = userService.deleteUser(uname);
        if (deletedUser == null) {
            return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to delete the user."), HttpStatus.OK);
        }
        notificationService.sendUserDeleted(deletedUser);
        return new ResponseEntity<>(CustomResponseEntity.getMessage("User successfully deleted!"), HttpStatus.OK);
    }

    @PutMapping("/users/update/{uname}")
    ResponseEntity<Object> updateUser(@PathVariable String uname, @RequestBody UserDTO user, @RequestHeader (name="Authorization") String token) {
        User updated = modelMapper.map(user, User.class);
        if (!user.getPassword().isEmpty()) {
            updated.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.getAuthorizations().stream().map(userRoleService::getRoleByTitle).forEach(updated::addRole);
        User old = userService.updateUser(uname, updated);
        if (old == null) {
            return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to update the user"), HttpStatus.OK);
        }
        String secretKey = "mySecretKey";
        notificationService.sendUserUpdated(old, updated, Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody()
                .getSubject());
        return new ResponseEntity<>(CustomResponseEntity.getMessage("User successfully updated!"), HttpStatus.OK);
    }

    @PatchMapping ("/users/status/{uname}")
    ResponseEntity<Object> updateUserStatus(@PathVariable String uname, @RequestParam(required = false) String verify){
        boolean check = Boolean.parseBoolean(verify);
        boolean result = userService.updateUserStatus(uname,check);
        if(!check){
            return new ResponseEntity<>(CustomResponseEntity.getMessage( "You attempted to log in with a wrong password more than 5 times. Your account has been deactivated"), HttpStatus.OK);
        }
        if(!result){
            return new ResponseEntity<>(CustomResponseEntity.getError("An error occurred while attempting to change user status"), HttpStatus.OK);
        }
        return new ResponseEntity<>(CustomResponseEntity.getMessage( "User status successfully updated!"), HttpStatus.OK);
    }

}

