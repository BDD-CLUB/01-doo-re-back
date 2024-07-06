package doore.team.application;

import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.보름;
import static doore.member.MemberFixture.아마스;
import static doore.member.MemberFixture.짱구;
import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.member.exception.MemberExceptionType.ALREADY_JOIN_TEAM_MEMBER;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static doore.team.exception.TeamExceptionType.NOT_MATCH_LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.TeamRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import doore.study.domain.CurriculumItem;
import doore.study.domain.ParticipantCurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.team.TeamFixture;
import doore.team.application.dto.request.TeamInviteCodeRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamCommandServiceTest extends IntegrationTest {
    @Autowired
    private TeamCommandService teamCommandService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRoleRepository teamRoleRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private CurriculumItemRepository curriculumItemRepository;
    @Autowired
    private ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    private Long teamId;
    private Long memberId;
    private TeamRole teamRole;

    @BeforeEach
    void setUp() {
        teamId = teamRepository.save(TeamFixture.team()).getId();
        memberId = memberRepository.save(미나()).getId();
        teamRole = TeamRole.builder()
                .teamRoleType(ROLE_팀장)
                .teamId(teamId)
                .memberId(memberId)
                .build();
        teamRoleRepository.save(teamRole);
    }

    @AfterEach
    void flushAll() {
        redisUtil.flushAll();
    }

    @Test
    @Disabled // 권한 처리 코드 주석 후 테스트 필요
    @DisplayName("[실패] 찾을 수 없는 팀은 팀 정보를 수정할 수 없다.")
    public void updateTeam_찾을_수_없는_팀은_팀_정보를_수정할_수_없다_실패() {
        //given
        final Long invalidId = 0L;
        final TeamUpdateRequest request = new TeamUpdateRequest("asdf", "asdf");

        //when & then
        assertThatThrownBy(() -> {
            teamCommandService.updateTeam(invalidId, request, memberId);
        }).isInstanceOf(TeamException.class)
                .hasMessage(NOT_FOUND_TEAM.errorMessage());
    }

    @Test
    @DisplayName("[실패] 팀장이 아니라면 팀 정보를 수정할 수 없다.")
    public void updateTeam_팀장이_아니라면_팀_정보를_수정할_수_없다_실패() {
        final Long notTeamLeaderMemberId = memberRepository.save(아마스()).getId();
        final TeamRole notTeamLeaderRole = TeamRole.builder()
                .teamRoleType(ROLE_팀원)
                .teamId(teamId)
                .memberId(notTeamLeaderMemberId)
                .build();
        teamRoleRepository.save(notTeamLeaderRole);

        final TeamUpdateRequest request = new TeamUpdateRequest("asdf", "asdf");

        assertThatThrownBy(() -> {
            teamCommandService.updateTeam(teamId, request, notTeamLeaderMemberId);
        }).isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
    }

    @Test
    @Disabled // TODO: 만료된 초대 링크 수정
    @DisplayName("[실패] 이미 가입된 팀원이라면 팀 가입을 할 수 없다.")
    public void joinTeam_이미_가입된_팀원이라면_팀_가입을_할_수_없다_실패() {
        final var createdCode = teamCommandService.generateTeamInviteCode(teamId).code();

        assertThatThrownBy(() -> {
            teamCommandService.joinTeam(teamId, new TeamInviteCodeRequest(createdCode), memberId);
        }).isInstanceOf(MemberException.class).hasMessage(ALREADY_JOIN_TEAM_MEMBER.errorMessage());
    }

    @Test
    @Disabled // TODO: 2/14/24 수정 S3 문제
    @DisplayName("[성공] 초대코드는 생성된다.")
    public void generateTeamInviteCode_초대코드는_생성된다_성공() {

        //when
        final var teamInviteLinkResponse = teamCommandService.generateTeamInviteCode(teamId);

        //then
        final Optional<String> data = redisUtil.getData("teamId:%d".formatted(teamId), String.class);
        assertThat(data).isNotEmpty();
        assertThat(data.get()).isEqualTo(teamInviteLinkResponse.code());
    }

    @Test
    @DisplayName("[성공] 이미 존재하는 초대코드가 있을 경우 초대코드를 반환한다.")
    public void generateTeamInviteCode_이미_존재하는_초대코드가_있을_경우_초대코드를_반환한다_성공() {
        //given
        final var createdCode = teamCommandService.generateTeamInviteCode(teamId).code();

        //when
        final var getCode = teamCommandService.generateTeamInviteCode(teamId).code();

        //then
        assertThat(createdCode).isEqualTo(getCode);
    }

    @Test
    @Disabled // TODO: 2/14/24 수정 S3 문제
    @DisplayName("[성공] 초대코드와 유저코드가 일치하면 팀 가입은 성공한다.")
    public void joinTeam_초대코드와_유저코드가_일치하면_팀_가입은_성공한다_성공() {
        //given
        final var createdCode = teamCommandService.generateTeamInviteCode(teamId).code();

        //when & then
        assertDoesNotThrow(() -> teamCommandService.joinTeam(teamId, new TeamInviteCodeRequest(createdCode), memberId));
    }

    @Test
    @DisplayName("[실패] 초대코드와 유저코드가 일치하지 않으면 팀 가입은 실패한다.")
    public void joinTeam_초대코드와_유저코드가_일치하지_않으면_팀_가입은_실패한다() {
        //given
        teamCommandService.generateTeamInviteCode(teamId).code();

        //when & then
        assertThatThrownBy(() -> {
            teamCommandService.joinTeam(teamId, new TeamInviteCodeRequest("invalid code"), memberId);
        }).isInstanceOf(TeamException.class)
                .hasMessage(NOT_MATCH_LINK.errorMessage());
    }

    @Nested
    @DisplayName("팀 삭제 시 연관 삭제 테스트")
    class DeleteTeamExpandTest {
        private CurriculumItem curriculumItem1;
        private CurriculumItem curriculumItem2;
        private ParticipantCurriculumItem participantCurriculumItem1;
        private ParticipantCurriculumItem participantCurriculumItem2;
        private Participant participant1;
        private Participant participant2;
        private Member member1;
        private Member member2;
        private Study study1;
        private Study study2;

        @BeforeEach
        void setUp() {
            study1 = studyRepository.save(algorithmStudy());
            study2 = studyRepository.save(algorithmStudy());
            member1 = memberRepository.save(보름());
            member2 = memberRepository.save(짱구());
            participant1 = participantRepository.save(
                    Participant.builder().studyId(study1.getId()).member(member1).build());
            participant2 = participantRepository.save(
                    Participant.builder().studyId(study2.getId()).member(member2).build());
            curriculumItem1 = curriculumItemRepository.save(
                    CurriculumItem.builder().id(1L).name("커리1").itemOrder(1).study(study1).build());
            curriculumItem2 = curriculumItemRepository.save(
                    CurriculumItem.builder().id(2L).name("커리2").itemOrder(2).study(study2).build());
            participantCurriculumItem1 = participantCurriculumItemRepository.save(
                    ParticipantCurriculumItem.builder().participantId(member1.getId())
                            .curriculumItem(curriculumItem1)
                            .build());
            participantCurriculumItem2 = participantCurriculumItemRepository.save(
                    ParticipantCurriculumItem.builder().participantId(member2.getId())
                            .curriculumItem(curriculumItem2)
                            .build());
        }

        @Test
        @Disabled // todo : S3 문제 해결 (06/02/24)
        @DisplayName("[성공] 팀이 삭제되면 연관된 스터디도 삭제된다.")
        void deleteTeam_팀이_삭제되면_연관된_스터디도_삭제된다() {
            final List<Study> beforeStudies = studyRepository.findAllByTeamId(teamId);
            assertThat(beforeStudies.size()).isEqualTo(2);

            teamCommandService.deleteTeam(teamId, memberId);
            final List<Study> afterStudies = studyRepository.findAllByTeamId(teamId);

            assertThat(afterStudies.get(0).getIsDeleted()).isEqualTo(true);
            assertThat(afterStudies.get(1).getIsDeleted()).isEqualTo(true);
        }

        @Test
        @Disabled // todo : S3 문제 해결 (06/02/24)
        @DisplayName("[성공] 팀이 삭제되면 연관된 커리큘럼도 삭제된다.")
        void deleteTeam_팀이_삭제되면_연관된_커리큘럼도_삭제된다() {
            final List<CurriculumItem> beforeCurriculumItems = curriculumItemRepository.findAll();

            teamCommandService.deleteTeam(teamId, memberId);
            final List<CurriculumItem> afterCurriculumItems = curriculumItemRepository.findAll();

            assertThat(beforeCurriculumItems.size()).isEqualTo(2);
            assertThat(afterCurriculumItems).isEmpty();
        }

        @Test
        @Disabled // todo : S3 문제 해결 (06/02/24)
        @DisplayName("[성공] 팀이 삭제되면 연관된 참여자 커리큘럼도 삭제된다.")
        void deleteTeam_팀이_삭제되면_연관된_참여자_커리큘럼_삭제된다() {
            final List<ParticipantCurriculumItem> beforeParticipantCurriculumItem = participantCurriculumItemRepository.findAll();

            teamCommandService.deleteTeam(teamId, memberId);
            final List<ParticipantCurriculumItem> afterParticipantCurriculumItem = participantCurriculumItemRepository.findAll();

            assertThat(beforeParticipantCurriculumItem.size()).isEqualTo(2);
            assertThat(afterParticipantCurriculumItem).isEmpty();
        }
    }
}
