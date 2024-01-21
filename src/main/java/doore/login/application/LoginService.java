package doore.login.application;

import doore.login.application.dto.GoogleAccountProfile;
import doore.login.application.dto.LoginResponse;
import doore.login.utils.GoogleClient;
import doore.login.utils.TokenGenerator;
import doore.member.application.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {
    private final MemberService memberService;
    private final GoogleClient googleClient;
    private final TokenGenerator tokenGenerator;

    public LoginResponse loginByGoogle(final String code) {
        final GoogleAccountProfile profile = googleClient.getGoogleAccountProfile(code);
        //final Member member = memberService.createOrFindBy(profile);
        final String token = tokenGenerator.generateToken(String.valueOf(1L));
        return new LoginResponse(1L, token);
    }
}
