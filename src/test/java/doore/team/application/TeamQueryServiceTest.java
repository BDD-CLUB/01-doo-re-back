package doore.team.application;

import static doore.member.MemberFixture.아마란스;
import static doore.team.TeamFixture.team;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.MemberTeamRepository;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TeamQueryServiceTest extends IntegrationTest {
    @Autowired
    TeamQueryService teamQueryService;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    MemberTeamRepository memberTeamRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] 내가 속한 스터디 목록을 조회할 수 있다.")
    void findMyStudies_내가_속한_스터디_목록을_조회할_수_있다_성공() {
        // given
        final Member member = memberRepository.save(아마란스());
        final Team myTeam = teamRepository.save(team());
        final Team otherTeam = teamRepository.save(team());
        memberTeamRepository.save(MemberTeam.builder()
                .teamId(myTeam.getId())
                .member(member)
                .isDeleted(false)
                .build());
        final List<TeamReferenceResponse> expectedResponses = List.of(TeamReferenceResponse.from(myTeam));

        // when
        final List<TeamReferenceResponse> actualResponses = teamQueryService.findMyTeams(member.getId());

        // then
        Assertions.assertThat(actualResponses)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponses);
    }

    // TODO: 3/21/24 자기 자신이 아닌 사람의 팀 목록을 조회하면 권한 예외가 발생한다.
}
