package doore.login.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleLoginRequest {
    private String code;

    public GoogleLoginRequest(final String code) {
        this.code = code;
    }
}
