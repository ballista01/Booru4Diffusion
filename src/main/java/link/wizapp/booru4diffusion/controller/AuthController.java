package link.wizapp.booru4diffusion.controller;

import link.wizapp.booru4diffusion.model.ERole;
import link.wizapp.booru4diffusion.model.Role;
import link.wizapp.booru4diffusion.model.User;
import link.wizapp.booru4diffusion.payload.request.LoginRequest;
import link.wizapp.booru4diffusion.payload.request.SignupRequest;
import link.wizapp.booru4diffusion.payload.response.JwtResponse;
import link.wizapp.booru4diffusion.payload.response.MessageResponse;
import link.wizapp.booru4diffusion.security.jwt.JwtUtils;
import link.wizapp.booru4diffusion.security.services.UserDetailsImpl;
import link.wizapp.booru4diffusion.tgw.IRoleTgw;
import link.wizapp.booru4diffusion.tgw.IUserTgw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    IUserTgw userTgw;

    @Autowired
    IRoleTgw roleTgw;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

//        List<String> manualRoles = userDetails.
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userTgw.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userTgw.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleTgw.findByName(ERole.ROLE_USER);
            if(userRole == null) throw new RuntimeException("Error: Role is not found.");
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleTgw.findByName(ERole.ROLE_ADMIN);
                        if(adminRole == null) throw new RuntimeException("Error: Role is not found.");
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleTgw.findByName(ERole.ROLE_MODERATOR);
                        if(modRole == null) throw new RuntimeException("Error: Role is not found.");
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleTgw.findByName(ERole.ROLE_USER);
                        if(userRole == null) throw new RuntimeException("Error: Role is not found.");
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userTgw.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}