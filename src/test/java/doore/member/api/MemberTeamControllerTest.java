package doore.member.api;

import static doore.member.MemberFixture.아마란스;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.MemberTeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberTeamControllerTest extends IntegrationTest {
    @Autowired
    private MemberTeamRepository memberTeamRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("정상적으로 팀원 목록을 조회한다.")
    void 정상적으로_팀원_목록을_조회한다_성공() throws Exception {
        final Member 아마란스 = memberRepository.save(아마란스());
        memberTeamRepository.save(
                MemberTeam.builder()
                        .teamId(1L)
                        .member(아마란스)
                        .isDeleted(false)
                        .build()
        );
        String url = "/teams/1/members";
        callGetApi(url).andExpect(status().isOk());
    }

}
