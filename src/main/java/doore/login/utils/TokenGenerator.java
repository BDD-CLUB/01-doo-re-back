package doore.login.utils;

import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {
    public String generateToken(final String id) {
        return "token";
    }
}
