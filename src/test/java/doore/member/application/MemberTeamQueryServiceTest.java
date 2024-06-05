package doore.member.application;

import static doore.member.MemberFixture.보름;
import static doore.member.MemberFixture.비비아마;
import static doore.member.MemberFixture.아마;
import static doore.member.MemberFixture.아마란스;
import static doore.member.MemberFixture.아마스;
import static doore.member.MemberFixture.아마어마어마;
import static doore.member.MemberFixture.짱구;
import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.team.TeamFixture.team;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import doore.helper.IntegrationTest;
import doore.member.application.dto.response.TeamMemberResponse;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.TeamRole;
import doore.member.domain.TeamRoleType;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberTeamQueryServiceTest extends IntegrationTest {
    @Autowired
    private MemberTeamQueryService memberTeamQueryService;
    @Autowired
    private MemberTeamRepository memberTeamRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRoleRepository teamRoleRepository;
    @Autowired
    private TeamRepository teamRepository;

    private Member 아마란스;
    private Member 아마어마어마;
    private Member 아마스;
    private Member 보름;
    private Member 짱구;
    private Member 비비아마;
    private Member 아마;
    private Team team;
    private TeamRole teamLeaderRole;
    private TeamRole teamMemberRole;

    @BeforeEach
    void setup() {
        아마란스 = memberRepository.save(아마란스());
        아마어마어마 = memberRepository.save(아마어마어마());
        아마스 = memberRepository.save(아마스());
        보름 = memberRepository.save(보름());
        짱구 = memberRepository.save(짱구());
        비비아마 = memberRepository.save(비비아마());
        아마 = memberRepository.save(아마());

        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(아마란스).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(아마어마어마).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(아마스).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(보름).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(짱구).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(1L).member(비비아마).isDeleted(false).build());
        memberTeamRepository.save(MemberTeam.builder().teamId(2L).member(아마).isDeleted(false).build());

        team = teamRepository.save(team());
        teamLeaderRole = teamRoleRepository.save(
                TeamRole.builder().teamId(team.getId()).memberId(보름.getId()).teamRoleType(ROLE_팀장).build());
        teamMemberRole = teamRoleRepository.save(
                TeamRole.builder().teamId(team.getId()).memberId(짱구.getId()).teamRoleType(ROLE_팀원).build());
        teamRoleRepository.save(
                TeamRole.builder().teamId(team.getId()).memberId(아마란스.getId()).teamRoleType(ROLE_팀원).build());
        teamRoleRepository.save(
                TeamRole.builder().teamId(team.getId()).memberId(아마어마어마.getId()).teamRoleType(ROLE_팀원).build());
        teamRoleRepository.save(
                TeamRole.builder().teamId(team.getId()).memberId(아마스.getId()).teamRoleType(ROLE_팀원).build());
        teamRoleRepository.save(
                TeamRole.builder().teamId(team.getId()).memberId(비비아마.getId()).teamRoleType(ROLE_팀원).build());
        teamRoleRepository.save(
                TeamRole.builder().teamId(team.getId()).memberId(아마.getId()).teamRoleType(ROLE_팀원).build());
    }

    @Test
    @DisplayName("[성공] 팀원들을 조회한다.")
    void findMemberTeams_팀원들을_조회한다_성공() {
        //given
        final List<Member> members = List.of(보름, 비비아마, 아마란스, 아마스, 아마어마어마, 짱구);
        final Map<Member, TeamRoleType> roleOfMembers = members.stream()
                .collect(toMap(
                        member -> member,
                        member -> teamRoleRepository.findTeamRoleByMemberId(member.getId())
                                .orElseThrow()
                                .getTeamRoleType()
                ));
        final List<TeamMemberResponse> expected = TeamMemberResponse.of(members, roleOfMembers);

        //when
        final List<TeamMemberResponse> actual = memberTeamQueryService.findMemberTeams(1L, null, teamMemberRole.getId());

        //then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("[성공] 키워드를 입력받으면 이름의 앞부분이 키워드와 일치하는 팀원들을 조회한다.")
    void findMemberTeams_키워드를_입력받으면_이름의_앞부분이_키워드와_일치하는_팀원들을_조회한다_성공() {
        //given
        final List<Member> members = List.of(아마스, 아마란스, 아마어마어마);
        final Map<Member, TeamRoleType> roleOfMembers = members.stream()
                .collect(toMap(
                        member -> member,
                        member -> teamRoleRepository.findTeamRoleByMemberId(member.getId())
                                .orElseThrow()
                                .getTeamRoleType()
                ));
        final List<TeamMemberResponse> expected = TeamMemberResponse.of(members, roleOfMembers);

        //when
        final List<TeamMemberResponse> actual = memberTeamQueryService.findMemberTeams(1L, "아마", teamMemberRole.getId());

        //then
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("[성공] 키워드를 입력받으면 이메일의 앞부분이 키워드와 일치하는 팀원들을 조회한다.")
    void findMemberTeams_키워드를_입력받으면_이메일의_앞부분이_키워드와_일치하는_팀원들을_조회한다_성공() {
        //given
        final List<Member> members = List.of(짱구, 아마스, 비비아마);
        final Map<Member, TeamRoleType> roleOfMembers = members.stream()
                .collect(toMap(
                        member -> member,
                        member -> teamRoleRepository.findTeamRoleByMemberId(member.getId())
                                .orElseThrow()
                                .getTeamRoleType()
                ));
        final List<TeamMemberResponse> expected = TeamMemberResponse.of(members, roleOfMembers);

        //when
        final List<TeamMemberResponse> actual = memberTeamQueryService.findMemberTeams(1L, "test", teamLeaderRole.getId());

        //then
        Assertions.assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
