package com.applix.shop.auth;

import com.applix.shop.users.User;
import com.applix.shop.users.UserRepository;
import com.applix.shop.users.UserRole;
import com.applix.shop.users.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final LegacyShaPasswordVerifier passwordVerifier;
    private final JwtService jwtService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return userRepository.findByLogin(request.login())
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .filter(user -> passwordVerifier.matches(request.password(), user.getPassword()))
                .map(this::respondWithToken)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    private ResponseEntity<LoginResponse> respondWithToken(User user) {
        // В легаси-схеме нет FK userswing.admin -> user_roles.idRole, поэтому
        // подстраховываемся на случай "битого" значения (роль могла быть удалена).
        String roleAuthority = userRoleRepository.findById(user.getAdmin())
                .map(UserRole::getAuthority)
                .orElse("ROLE_USER");

        String token = jwtService.generateToken(user.getId(), user.getLogin(), roleAuthority);
        return ResponseEntity.ok(new LoginResponse(token, user.getLogin(), roleAuthority));
    }
}


