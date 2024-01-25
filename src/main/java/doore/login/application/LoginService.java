package doore.login.application;

import doore.login.application.dto.request.GoogleLoginRequest;
import doore.login.application.dto.response.GoogleAccountProfileResponse;
import doore.login.application.dto.response.LoginResponse;
import doore.login.utils.GoogleClient;
import doore.login.utils.JwtTokenGenerator;
import doore.member.application.MemberCommandService;
import doore.member.domain.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {
    private final MemberCommandService memberCommandService;
    private final GoogleClient googleClient;
    private final JwtTokenGenerator jwtTokenGenerator;

    public LoginResponse loginByGoogle(final GoogleLoginRequest request) {
        final GoogleAccountProfileResponse profile = googleClient.getGoogleAccountProfile(request.getCode());
        final Member member = memberCommandService.findOrCreateMemberBy(profile);
        final String token = jwtTokenGenerator.generateToken(String.valueOf(member.getId()));
        return new LoginResponse(member.getId(), token);
    }
}
