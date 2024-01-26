package doore.login.utils;

import static org.assertj.core.api.Assertions.assertThat;

import doore.helper.IntegrationTest;
import doore.login.exception.LoginException;
import doore.login.exception.LoginExceptionType;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TokenGeneratorTest extends IntegrationTest {

    @Autowired
    private JwtTokenGenerator tokenGenerator;

    @Test
    @DisplayName("[성공] 회원의 아이디를 입력하면 토큰을 생성할 수 있다")
    void 회원의_아이디를_입력하면_토큰을_생성할_수_있다() {
        //given
        final String extractedMemberId = "1";

        //when
        final String token = tokenGenerator.generateToken(extractedMemberId);
        final String actualMemberId = tokenGenerator.extractMemberId(token);

        //then
        assertThat(actualMemberId).isEqualTo(extractedMemberId);
    }


    @Test
    @DisplayName("[실패] 유효하지 않은 토큰을 입력하면 예외가 발생한다 실패")
    void 유효하지_않은_토큰을_입력하면_예외가_발생한다_실패() {
        //given
        final String invalidToken = "invalid_token";

        //when
        final ThrowingCallable actual = () -> tokenGenerator.extractMemberId(invalidToken);

        //then
        Assertions.assertThatThrownBy(actual)
                .isInstanceOf(LoginException.class)
                .hasMessage(LoginExceptionType.INVALID_ACCESS_TOKEN.errorMessage());

    }

}
