package doore.login.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import doore.helper.ServiceIntegrationTestHelper;
import doore.login.application.dto.GoogleAccountProfile;
import doore.login.application.dto.LoginResponse;
import doore.login.utils.TokenGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LoginServiceTest extends ServiceIntegrationTestHelper {
    @Autowired
    private LoginService loginService;
    @Autowired
    private TokenGenerator tokenGenerator;

    @Test
    @DisplayName("로그인 요청 시 로그인 토큰 정보를 반환한다 성공")
    void 로그인_요청_시_로그인_토큰_정보를_반환한다_성공() {
        //given
        final String code = "valid_code";
        final Long memberId = 1L;
        final String token = tokenGenerator.generateToken(String.valueOf(memberId));
        final LoginResponse expected = new LoginResponse(memberId, token);
        final GoogleAccountProfile profile = new GoogleAccountProfile();
        given(googleClient.getGoogleAccountProfile(code)).willReturn(profile);

        //when
        final LoginResponse actual = loginService.loginByGoogle(code);

        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
