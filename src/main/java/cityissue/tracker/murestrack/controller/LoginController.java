package cityissue.tracker.murestrack.controller;

import cityissue.tracker.murestrack.dto.UserDTO;
import cityissue.tracker.murestrack.persistence.model.CustomResponseEntity;
import cityissue.tracker.murestrack.persistence.model.User;
import cityissue.tracker.murestrack.persistence.model.UserRole;
import cityissue.tracker.murestrack.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class LoginController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    @Autowired
    public LoginController(UserService userService, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/login")
    ResponseEntity<Object> login(@RequestBody Map<String, String> userCredentials){
        String uname = userCredentials.get("username");
        String password = userCredentials.get("password");
        User user = userService.getByUsername(uname);
        if(user == null){
            return new ResponseEntity<>(CustomResponseEntity.getError("Invalid username!"), HttpStatus.OK);
        }
        if(!passwordEncoder.matches(password, user.getPassword())) {
            return new ResponseEntity<>(CustomResponseEntity.getError("Invalid password!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(getUserDTOWithToken(user), HttpStatus.OK);
    }

    @GetMapping("/logout/{uname}")
    ResponseEntity<Object> logout(@PathVariable String uname){
//        User user = userService.getByUsername(uname);
//        userService.updateUser(uname, user);
        return new ResponseEntity<>(CustomResponseEntity.getMessage("Logged out successfully!"), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    ResponseEntity<Object> refreshToken(@RequestBody Map<String, String> userCredentials){
        String uname = userCredentials.get("username");
        User user = userService.getByUsername(uname);
        if(user == null){
            return new ResponseEntity<>(CustomResponseEntity.getError("Unauthorized!"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(getUserDTOWithToken(user), HttpStatus.OK);
    }

    private UserDTO getUserDTOWithToken(User user) {
        String token = getJWTToken(user.getUsername());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setToken(token);
        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(UserRole::getTitle)
                        .collect(Collectors.toList());
        userDTO.setAuthorizations(roles);
        return userDTO;
    }

    private String getJWTToken(String username) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (3 * 60 * 1000)))
                .signWith(SignatureAlgorithm.HS512,
                        secretKey.getBytes()).compact();

        return token;
    }

}
