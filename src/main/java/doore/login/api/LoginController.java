package doore.login.api;

import doore.login.application.LoginService;
import doore.login.application.dto.LoginResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/google")
    public ResponseEntity<LoginResponse> loginByGoogle(@RequestBody final String code) {
        return ResponseEntity.ok(loginService.loginByGoogle(code));
    }
}
