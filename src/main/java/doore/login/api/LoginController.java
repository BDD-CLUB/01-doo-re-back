package doore.login.api;

import doore.login.application.LoginService;
import doore.login.application.dto.request.GoogleLoginRequest;
import doore.login.application.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> loginByGoogle(@Valid @RequestBody final GoogleLoginRequest request) {
        return ResponseEntity.ok(loginService.loginByGoogle(request));
    }
}
