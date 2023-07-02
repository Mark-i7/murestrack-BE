package cityissue.tracker.murestrack.service.impl;

import cityissue.tracker.murestrack.persistence.model.*;
import cityissue.tracker.murestrack.persistence.model.validator.EntityValidator;
import cityissue.tracker.murestrack.persistence.repository.UserRepository;
import cityissue.tracker.murestrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EntityValidator<User> userValidator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EntityValidator<User> userValidator){
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<Permission> getUserPermissions(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        List<Permission> permissions = new ArrayList<>();
        if(user.isPresent()){
            permissions = user.get().getRoles()
                    .stream()
                    .map(UserRole::getPermissions)
                    .flatMap(Set::stream)
                    .collect(Collectors.toList());
        }
        return permissions;
    }

    private String generateUsername(User user){
        String username = user.getLastName()
                .substring(0, Math.min(user.getLastName().length(), 5))
                .concat(String.valueOf(user.getFirstName().charAt(0)))
                .toLowerCase(Locale.ROOT);

        int i = 1;
        while(userRepository.findByUsername(username).isPresent() && i < user.getFirstName().length()){
            username += user.getFirstName().charAt(i);
            i++;
        }
        if(userRepository.findByUsername(username).isEmpty()){
            return username;
        }
        i = 1;
        while(userRepository.findByUsername(username.concat(i + "")).isPresent()){
            i++;
        }
        return username.concat(i + "");
    }



    @Override
    public boolean addUser(User user) {
        List<String> violations = userValidator.validate(user);
        if(!violations.isEmpty()){
            return false;
        }
        user.setUsername(generateUsername(user));
        user.setActive(UserStatus.ACTIVE);
        this.userRepository.save(user);
        return true;
    }

    @Override
    public User deleteUser(String uname) {
        Optional<User> opt = userRepository.findByUsername(uname);
        opt.ifPresent(user -> userRepository.deleteById(user.getId()));
        return opt.orElse(null);
    }

    @Override
    @Transactional
    public User updateUser(String uname, User updated) {
        List<String> violations = userValidator.validate(updated);
        if(!violations.isEmpty()){
            return null;
        }
        Optional<User> opt = userRepository.findByUsername(uname);
        if(opt.isEmpty()){
            return null;
        }
        User toUpdate = opt.get();
        User oldUser = toUpdate.toBuilder().build();
        toUpdate.setFirstName(updated.getFirstName());
        toUpdate.setLastName(updated.getLastName());
        toUpdate.setEmail(updated.getEmail());
        toUpdate.setPhoneNumber(updated.getPhoneNumber());
        toUpdate.removeRoles(toUpdate.getRoles());
        toUpdate.addRoles(updated.getRoles());
        if(!updated.getPassword().isEmpty()){
            toUpdate.setPassword(updated.getPassword());
        }
        userRepository.save(toUpdate);
        return oldUser;
    }


    @Override
    @Transactional
    public boolean updateUserStatus(String uname, boolean validation) {
        Optional<User> maybeUser = this.userRepository.findByUsername(uname);
        if(maybeUser.isEmpty()){
            return false;
        }
        User user = maybeUser.get();
        if(!validation){
            if(user.getActive() == UserStatus.ACTIVE) {
                user.setActive(UserStatus.INACTIVE);
                userRepository.save(user);
                return true;
            }
            return false;
        }
        if(user.getActive() == UserStatus.INACTIVE){
            user.setActive(UserStatus.ACTIVE);
            userRepository.save(user);
            return true;
        }
        if(user.getAssignedReports().stream().anyMatch(report->report.getStatus() != Status.CLOSED)){
            return false;
        }
        user.setActive(UserStatus.INACTIVE);
        userRepository.save(user);
        return true;

    }

}
