package doore.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import doore.helper.IntegrationTest;
import doore.login.application.dto.response.GoogleAccountProfileResponse;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberCommandServiceTest extends IntegrationTest {
    @Autowired
    private MemberCommandService memberCommandService;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void init() {
        // TODO: 1/23/24 fixture
        member = memberRepository.save(new Member("아마란스", "1234", "bbb@gmail.com", "https://aaa"));
    }

    @Test
    @DisplayName("[성공] 이미 등록된 회원이 있다면 해당 회원을 반환한다")
    void 이미_등록된_회원이_있다면_해당_회원을_반환한다_성공() {
        //given
        final GoogleAccountProfileResponse profile = new GoogleAccountProfileResponse(member.getGoogleId(),
                "bbb@gmail.com", true, "아마란스", "마란스", "아", "https://aaa", "ko");

        //when
        final Member actual = memberCommandService.findOrCreateMemberBy(profile);

        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(member);
    }

    @Test
    @DisplayName("[성공] 등록된 회원이 없다면 회원을 새로 추가하고 반환한다")
    void 등록된_회원이_없다면_회원을_새로_추가하고_반환한다_성공() {
        //given
        final Long beforeCount = memberRepository.count();
        final String newMemberGoogleId = "4321";
        final GoogleAccountProfileResponse profile = new GoogleAccountProfileResponse(newMemberGoogleId,
                "ccc@gmail.com", true, "프리지아", "리지아", "프", "https://aaa", "ko");

        //when
        final Member newMember = memberCommandService.findOrCreateMemberBy(profile);
        final Long afterCount = memberRepository.count();

        //then
        assertAll(
                () -> assertThat(newMember.getGoogleId()).isEqualTo(newMemberGoogleId),
                () -> assertThat(afterCount).isEqualTo(beforeCount + 1)
        );
    }

}
