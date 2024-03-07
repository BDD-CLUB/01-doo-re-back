package doore.login.application;

import static doore.member.MemberFixture.아마란스;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import doore.helper.IntegrationTest;
import doore.login.application.dto.request.GoogleLoginRequest;
import doore.login.application.dto.response.GoogleAccountProfileResponse;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LoginServiceTest extends IntegrationTest {
    @Autowired
    private LoginService loginService;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void init() {
        member = memberRepository.save(아마란스());
    }

    @Test
    @DisplayName("[성공] 이미 회원가입된 사용자가 구글 로그인 요청 시 로그인 토큰 정보를 반환한다")
    void loginByGoogle_이미_회원가입된_사용자가_구글_로그인_요청_시_로그인_토큰_정보를_반환한다_성공() {
        //given
        final Long expectedMemberId = member.getId();
        final String validCode = "valid_code";
        final GoogleLoginRequest request = new GoogleLoginRequest(validCode);
        final GoogleAccountProfileResponse profile = new GoogleAccountProfileResponse(
                member.getGoogleId(), "aaa@gmail.com", true, "아마란스", "마란스", "아", "https://aaa", "ko"
        );
        given(googleClient.getGoogleAccountProfile(validCode)).willReturn(profile);

        //when
        final Long actualMemberId = loginService.loginByGoogle(request)
                .memberId();

        //then
        assertThat(actualMemberId).usingRecursiveComparison()
                .isEqualTo(expectedMemberId);
    }

    @Test
    @DisplayName("[성공] 새로운 회원이 구글 로그인 요청 시 회원 정보를 등록하고 로그인 토큰 정보를 반환한다")
    void loginByGoogle_새로운_회원이_구글_로그인_요청_시_회원_정보를_등록하고_로그인_토큰_정보를_반환한다_성공() {
        //given
        final Long beforeCount = memberRepository.count();
        final String code = "valid_code";
        final String googleId = "4321";
        final GoogleLoginRequest request = new GoogleLoginRequest(code);
        final GoogleAccountProfileResponse profile = new GoogleAccountProfileResponse(
                googleId, "ppp@gmail.com", true, "프리지아", "리지아", "프", "https://aaa", "ko"
        );
        given(googleClient.getGoogleAccountProfile(code)).willReturn(profile);

        //when
        final Long actual = loginService.loginByGoogle(request)
                .memberId();
        final Long afterCount = memberRepository.count();
        final Long newMemberId = memberRepository.findByGoogleId(googleId).get().getId();

        //then
        assertAll(
                () -> assertThat(actual).usingRecursiveComparison()
                        .isEqualTo(newMemberId),
                () -> assertThat(afterCount).isEqualTo(beforeCount + 1)
        );
    }
}
