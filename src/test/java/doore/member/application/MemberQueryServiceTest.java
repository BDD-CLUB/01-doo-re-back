package doore.member.application;

        import static doore.member.MemberFixture.미나;
        import static doore.member.domain.StudyRoleType.ROLE_스터디장;
        import static doore.member.domain.TeamRoleType.ROLE_팀원;
        import static doore.member.domain.TeamRoleType.ROLE_팀장;
        import static doore.study.StudyFixture.algorithmStudy;
        import static doore.team.TeamFixture.team;
        import static org.assertj.core.api.Assertions.assertThat;

        import doore.helper.IntegrationTest;
        import doore.member.application.dto.response.MemberAndMyTeamsAndStudiesResponse;
        import doore.member.domain.Member;
        import doore.member.domain.MemberTeam;
        import doore.member.domain.Participant;
        import doore.member.domain.StudyRole;
        import doore.member.domain.TeamRole;
        import doore.member.domain.repository.MemberRepository;
        import doore.member.domain.repository.MemberTeamRepository;
        import doore.member.domain.repository.ParticipantRepository;
        import doore.member.domain.repository.StudyRoleRepository;
        import doore.member.domain.repository.TeamRoleRepository;
        import doore.study.domain.Study;
        import doore.study.domain.repository.StudyRepository;
        import doore.team.application.TeamQueryService;
        import doore.team.domain.Team;
        import doore.team.domain.TeamRepository;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.DisplayName;
        import org.junit.jupiter.api.Test;
        import org.springframework.beans.factory.annotation.Autowired;

public class MemberQueryServiceTest extends IntegrationTest {

    @Autowired
    private MemberQueryService memberQueryService;
    @Autowired
    private TeamQueryService teamQueryService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberTeamRepository memberTeamRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRoleRepository teamRoleRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private StudyRoleRepository studyRoleRepository;

    private Member member;
    private Team team;
    private Team otherTeam;
    private MemberTeam memberTeam;
    private MemberTeam otherMemberTeam;
    private Study study;
    private Participant participant;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(미나());
        team = teamRepository.save(team());
        otherTeam = teamRepository.save(team());
        study = studyRepository.save(algorithmStudy());

        memberTeam = memberTeamRepository.save(MemberTeam.builder().teamId(team.getId()).member(member).build());
        teamRoleRepository.save(TeamRole.builder()
                .teamId(team.getId())
                .teamRoleType(ROLE_팀장)
                .memberId(member.getId())
                .build());

        otherMemberTeam = memberTeamRepository.save(
                MemberTeam.builder().teamId(otherTeam.getId()).member(member).build());
        teamRoleRepository.save(TeamRole.builder()
                .teamId(otherTeam.getId())
                .teamRoleType(ROLE_팀원)
                .memberId(member.getId())
                .build());

        participant = participantRepository.save(Participant.builder().member(member).studyId(study.getId()).build());
        studyRoleRepository.save(StudyRole.builder()
                .studyId(study.getId())
                .studyRoleType(ROLE_스터디장)
                .memberId(member.getId())
                .build());
    }

    @Test
    @DisplayName("[성공] 나의 팀과 스터디 목록을 조회할 수 있다.")
    void getSideBarInfo_나의_팀과_스터디_목록을_조회할_수_있다_성공() {
        final MemberAndMyTeamsAndStudiesResponse expected = MemberAndMyTeamsAndStudiesResponse.of(member,
                teamQueryService.getMyTeamsAndStudies(member.getId()));
        final MemberAndMyTeamsAndStudiesResponse actual = memberQueryService.getSideBarInfo(member.getId(),
                member.getId());

        assertThat(expected).isEqualTo(actual);
        assertThat(actual.myTeamsAndStudies().size()).isEqualTo(2);
        assertThat(actual.myTeamsAndStudies().get(0).teamStudies().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("[성공] 삭제된 팀은 나의 팀 내역에 포함되지 않는다.")
    void getSideBarInfo_삭제된_팀은_나의_팀_내역에_포함되지_않는다_성공() {
        memberTeamRepository.deleteByTeamIdAndMemberId(otherTeam.getId(), member.getId());

        final MemberAndMyTeamsAndStudiesResponse actual = memberQueryService.getSideBarInfo(member.getId(),
                member.getId());

        assertThat(actual.myTeamsAndStudies().size()).isEqualTo(1);
        assertThat(actual.myTeamsAndStudies().get(0).teamStudies().size()).isEqualTo(1);
    }
}
