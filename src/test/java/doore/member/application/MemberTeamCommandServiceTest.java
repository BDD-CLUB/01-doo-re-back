package doore.member.application;

import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.보름;
import static doore.member.MemberFixture.아마란스;
import static doore.member.MemberFixture.짱구;
import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.member.exception.MemberExceptionType.CANNOT_DELETE_STUDY_LEADER;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.member.exception.MemberTeamExceptionType.CANNOT_DELETE_TEAM_LEADER_SELF;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.StudyRoleType;
import doore.member.domain.TeamRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import doore.member.exception.MemberTeamException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberTeamCommandServiceTest extends IntegrationTest {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberTeamRepository memberTeamRepository;
    @Autowired
    private TeamRoleRepository teamRoleRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private StudyRoleRepository studyRoleRepository;
    @Autowired
    private MemberTeamCommandService memberTeamCommandService;

    private Member teamLeader;
    private Member teamMember;
    private Member otherTeamMember;
    private Team team;
    private Team otherTeam;
    private TeamRole teamRoleLeader;
    private TeamRole teamRoleMember;
    private TeamRole otherTeamRoleMember;
    private MemberTeam memberTeamLeader;
    private MemberTeam memberTeamMember;

    @BeforeEach
    void setup() {
        teamLeader = 아마란스();
        teamMember = 보름();
        otherTeamMember = 짱구();
        memberRepository.saveAll(List.of(teamLeader, teamMember, otherTeamMember));
        team = team();
        otherTeam = team();
        teamRepository.saveAll(List.of(team, otherTeam));
        teamRoleLeader = TeamRole.builder()
                .memberId(teamLeader.getId())
                .teamRoleType(ROLE_팀장)
                .teamId(team.getId())
                .build();
        teamRoleMember = TeamRole.builder()
                .memberId(teamMember.getId())
                .teamRoleType(ROLE_팀원)
                .teamId(team.getId())
                .build();
        otherTeamRoleMember = TeamRole.builder()
                .memberId(otherTeamMember.getId())
                .teamRoleType(ROLE_팀원)
                .teamId(otherTeam.getId())
                .build();
        teamRoleRepository.saveAll(List.of(teamRoleLeader, teamRoleMember, otherTeamRoleMember));
        memberTeamLeader = MemberTeam.builder()
                .teamId(team.getId())
                .member(teamLeader)
                .isDeleted(false)
                .build();
        memberTeamMember = MemberTeam.builder()
                .teamId(team.getId())
                .member(teamMember)
                .isDeleted(false)
                .build();
        memberTeamRepository.saveAll(List.of(memberTeamLeader, memberTeamMember));
    }

    @Test
    @DisplayName("[성공] 팀장은 정상적으로 팀원을 삭제할 수 있다.")
    public void deleteMemberTeam_팀장은_정상적으로_팀원을_삭제할_수_있다_성공() throws Exception {
        long memberCount = memberTeamRepository.countByTeamId(team.getId());
        //when
        memberTeamCommandService.deleteMemberTeam(team.getId(), teamMember.getId(), teamLeader.getId());

        //then
        long resultMemberCount = memberTeamRepository.countByTeamId(team.getId());
        assertThat(resultMemberCount).isEqualTo(memberCount - 1);
    }

    @Test
    @DisplayName("[실패] 팀장은 다른 팀의 팀원을 삭제할 수 없다.")
    public void deleteMemberTeam_팀장은_다른_팀의_팀원을_삭제할_수_없다_실패() throws Exception {
        //when & then
        assertThatThrownBy(() -> {
            memberTeamCommandService.deleteMemberTeam(team.getId(), otherTeamMember.getId(), teamLeader.getId());
        }).isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @DisplayName("[실패] 팀장은 팀장 본인을 탈퇴 시킬 수 없다.")
    public void deleteMemberTeam_팀장은_팀장_본인을_탈퇴_시킬_수_없다_실패() throws Exception {
        assertThatThrownBy(() -> {
            memberTeamCommandService.deleteMemberTeam(team.getId(), teamLeader.getId(), teamLeader.getId());
        }).isInstanceOf(MemberTeamException.class).hasMessage(CANNOT_DELETE_TEAM_LEADER_SELF.errorMessage());
    }

    @Test
    @DisplayName("[실패] 팀장은 스터디장인 팀원을 탈퇴 시킬 수 없다.")
    public void deleteMemberTeam_팀장은_스터디장인_팀원을_탈퇴_시킬_수_없다_실패() throws Exception {
        final Study study = studyRepository.save(algorithmStudy());
        final Member studyLeaderMember = memberRepository.save(미나());
        memberTeamRepository.save(MemberTeam.builder()
                .member(studyLeaderMember)
                .teamId(team.getId())
                .build());
        teamRoleRepository.save(TeamRole.builder()
                .memberId(studyLeaderMember.getId())
                .teamRoleType(ROLE_팀원)
                .teamId(team.getId())
                .build());
        participantRepository.save(Participant.builder()
                .member(studyLeaderMember)
                .studyId(study.getId())
                .build());
        studyRoleRepository.save(StudyRole.builder()
                .studyId(study.getId())
                .studyRoleType(StudyRoleType.ROLE_스터디장)
                .memberId(studyLeaderMember.getId())
                .build());

        assertThatThrownBy(() -> {
            memberTeamCommandService.deleteMemberTeam(team.getId(), studyLeaderMember.getId(), teamLeader.getId());
        }).isInstanceOf(MemberException.class).hasMessage(CANNOT_DELETE_STUDY_LEADER.errorMessage());
    }
}
