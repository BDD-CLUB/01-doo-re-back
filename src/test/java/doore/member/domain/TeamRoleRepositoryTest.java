package doore.member.domain;

import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static org.assertj.core.api.Assertions.assertThat;

import doore.helper.RepositorySliceTest;
import doore.member.MemberFixture;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.team.TeamFixture;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamRoleRepositoryTest extends RepositorySliceTest {

    @Autowired
    private TeamRoleRepository teamRoleRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("[성공] 팀 정보와 멤버 정보가 유효하다면 팀 내 역할을 조회할 수 있다")
    void findTeamRoleByTeamIdAndMemberId_팀_정보와_멤버_정보가_유효하다면_팀_내_역할을_조회할_수_있다() {
        Team team = TeamFixture.team();
        teamRepository.save(team);
        Member member = MemberFixture.회원();
        memberRepository.save(member);
        TeamRole teamRole = TeamRole.builder()
                .teamId(team.getId())
                .teamRoleType(ROLE_팀원)
                .memberId(member.getId())
                .build();
        teamRoleRepository.save(teamRole);
        em.flush();
        em.clear();

        Optional<TeamRole> result = teamRoleRepository.findTeamRoleByTeamIdAndMemberId(team.getId(), member.getId());

        assertThat(result).isNotEmpty();
    }
}
