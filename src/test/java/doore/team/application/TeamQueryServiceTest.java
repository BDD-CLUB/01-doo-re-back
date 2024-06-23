package doore.team.application;

import static doore.member.MemberFixture.createMember;
import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.아마란스;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.team.TeamFixture.createTeam;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.attendance.domain.Attendance;
import doore.attendance.domain.repository.AttendanceRepository;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.exception.MemberException;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.application.dto.response.TeamResponse;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TeamQueryServiceTest extends IntegrationTest {
    @Autowired
    private TeamQueryService teamQueryService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberTeamRepository memberTeamRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(미나());
    }

    @Test
    @DisplayName("[성공] 내가 속한 팀 목록을 조회할 수 있다.")
    void findMyTeams_내가_속한_팀_목록을_조회할_수_있다_성공() {
        // given
        final Member member = memberRepository.save(아마란스());
        final Team myTeam = teamRepository.save(team());
        final Team otherTeam = teamRepository.save(team());
        final Long tokenMemberId = member.getId();
        memberTeamRepository.save(MemberTeam.builder()
                .teamId(myTeam.getId())
                .member(member)
                .isDeleted(false)
                .build());
        final List<TeamReferenceResponse> expectedResponses = List.of(TeamReferenceResponse.from(myTeam));

        // when
        final List<TeamReferenceResponse> actualResponses = teamQueryService.findMyTeams(member.getId(), tokenMemberId);

        // then
        Assertions.assertThat(actualResponses)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponses);
    }

    // TODO: 3/21/24 자기 자신이 아닌 사람의 팀 목록을 조회하면 권한 예외가 발생한다. (2024/5/14 완료)
    @Test
    @DisplayName("[실패] 다른 사람의 팀 목록 조회는 불가능하다.")
    void findMyTeams_다른_사람의_팀_목록_조회는_불가능하다_실패() {
        final Long anotherMemberId = 2L;
        // 로그인 되어있는 아이디와 조회하려는 아이디가 다른 경우 실패 (주석은 확인 후 삭제할 예정입니다.)
        assertThatThrownBy(() -> {
            teamQueryService.findMyTeams(member.getId(), anotherMemberId);
        }).isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[성공] 팀 상세 조회를 할 수 있다.")
    void findTeamByTeamId_팀_상세_조회를_할_수_있다_성공() {
        final Team team = createTeam();
        final Member anotherMember = createMember();
        memberTeamRepository.save(MemberTeam.builder().teamId(team.getId()).member(member).isDeleted(false).build());
        memberTeamRepository.save(
                MemberTeam.builder().teamId(team.getId()).member(anotherMember).isDeleted(false).build());
        attendanceRepository.save(Attendance.builder().memberId(anotherMember.getId()).build());

        final long attendanceRatio = 50L;

        final TeamResponse expectTeamResponse = TeamResponse.of(team, attendanceRatio);
        final TeamResponse actualTeamResponse = teamQueryService.findTeamByTeamId(team.getId());

        assertThat(actualTeamResponse).isEqualTo(expectTeamResponse);
    }
}
