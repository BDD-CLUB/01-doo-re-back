package doore.member.api;

import static doore.member.MemberFixture.미나;
import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.team.TeamFixture.team;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.TeamRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberTeamControllerTest extends IntegrationTest {
    @Autowired
    private MemberTeamRepository memberTeamRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamRoleRepository teamRoleRepository;

    private Member member;
    private Team team;
    private TeamRole teamRole;
    private String token;

    @BeforeEach
    void setUp() {
        team = teamRepository.save(team());
        member = memberRepository.save(미나());
        teamRole = teamRoleRepository.save(TeamRole.builder()
                .teamRoleType(ROLE_팀원)
                .memberId(member.getId())
                .teamId(team.getId())
                .build());
        token = jwtTokenGenerator.generateToken(String.valueOf(member.getId()));
    }

    @Test
    @DisplayName("정상적으로 팀원 목록을 조회한다.")
    void 정상적으로_팀원_목록을_조회한다_성공() throws Exception {
        memberTeamRepository.save(
                MemberTeam.builder()
                        .teamId(team.getId())
                        .member(member)
                        .isDeleted(false)
                        .build()
        );
        final String url = "/teams/1/members";
        callGetApi(url, token).andExpect(status().isOk());
    }

}
