package doore.study.application;

import static doore.member.MemberFixture.createMember;
import static doore.member.MemberFixture.미나;
import static doore.member.MemberFixture.아마란스;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.member.exception.ParticipantExceptionType.ALREADY_JOINED_STUDY;
import static doore.member.exception.ParticipantExceptionType.CANNOT_DELETE_STUDY_LEADER_SELF;
import static doore.study.StudyFixture.algorithmStudy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.IntegrationTest;
import doore.member.MemberFixture;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.TeamRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import doore.member.exception.ParticipantException;
import doore.study.application.dto.response.ParticipantResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantCommandServiceTest extends IntegrationTest {
    @Autowired
    private ParticipantCommandService participantCommandService;
    @Autowired
    private ParticipantQueryService participantQueryService;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudyRoleRepository studyRoleRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private TeamRoleRepository teamRoleRepository;

    private Member teamLeader;
    private Member member;
    private Study study;
    private StudyRole studyRole;
    private TeamRole teamLeaderRole;
    private TeamRole teamRole;

    @BeforeEach
    void setUp() {
        teamLeader = memberRepository.save(미나());
        member = memberRepository.save(아마란스());
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(ROLE_스터디장)
                .studyId(study.getId())
                .memberId(member.getId())
                .build());
        teamLeaderRole = teamRoleRepository.save(TeamRole.builder()
                .teamId(study.getTeamId())
                .teamRoleType(ROLE_팀장)
                .memberId(teamLeader.getId())
                .build());
        teamRole = teamRoleRepository.save(TeamRole.builder()
                .teamId(study.getTeamId())
                .teamRoleType(ROLE_팀원)
                .memberId(member.getId())
                .build());
    }

    @Nested
    @DisplayName("참여자 Command 테스트")
    class participantTest {

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 추가할 수 있다.")
        void createParticipant_정상적으로_참여자를_추가할_수_있다_성공() {
            //Given
            final Long studyId = study.getId();
            final Long memberId = member.getId();

            //when
            participantCommandService.createParticipant(studyId, memberId, member.getId());

            //then
            final List<ParticipantResponse> participantResponses = participantQueryService.getParticipants(studyId,
                    memberId);
            assertAll(
                    () -> assertThat(participantResponses).hasSize(1),
                    () -> assertEquals(memberId, participantResponses.get(0).memberId())
            );
        }

        @Test
        @DisplayName("[실패] 이미 스터디에 가입되어 있다면 참여자 추가는 실패한다.")
        void createParticipant_이미_스터디에_가입되어_있다면_참여자_추가는_실패한다_실패() {
            participantCommandService.createParticipant(study.getId(), member.getId(), member.getId());

            assertThatThrownBy(
                    () -> participantCommandService.createParticipant(study.getId(), member.getId(),
                            member.getId()))
                    .isInstanceOf(ParticipantException.class).hasMessage(ALREADY_JOINED_STUDY.errorMessage());
        }

        @Test
        @DisplayName("[성공] 스터디 탈퇴 후 스터디 재참여 시 참여자 추가 가능하다.")
        void createParticipant_스터디_탈퇴_후_스터디_재참여_시_참여자_추가_가능하다_성공() {
            final Member anotherMember = memberRepository.save(MemberFixture.아마스());
            teamRoleRepository.save(TeamRole.builder()
                    .memberId(anotherMember.getId())
                    .teamRoleType(ROLE_팀원)
                    .teamId(study.getTeamId())
                    .build());

            participantCommandService.createParticipant(study.getId(), anotherMember.getId(), member.getId());
            participantCommandService.withdrawParticipant(study.getId(), anotherMember.getId());

            participantCommandService.createParticipant(study.getId(), anotherMember.getId(), member.getId());

            assertThat(participantRepository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("[실패] 추가하려는 참여자가 팀원이 아닌 경우 참여자 추가는 실패한다.")
        void createParticipant_추가하려는_참여자가_팀원이_아닌_경우_참여자_추가는_실패한다_실패() {
            final Member notMemberTeam = createMember();
            assertThatThrownBy(
                    () -> participantCommandService.createParticipant(study.getId(), notMemberTeam.getId(),
                            member.getId()))
                    .isInstanceOf(MemberException.class).hasMessage(UNAUTHORIZED.errorMessage());
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 회원인 경우 실패한다.")
        void notExistMember_존재하지_않는_회원인_경우_실패한다_실패() {
            final Long notExistingMemberId = 50L;

            assertThatThrownBy(
                    () -> participantCommandService.createParticipant(study.getId(), notExistingMemberId,
                            member.getId()))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(UNAUTHORIZED.errorMessage());
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 삭제할 수 있다.")
        void deleteParticipant_정상적으로_참여자를_삭제할_수_있다_성공() {
            //Given
            final Long studyId = study.getId();
            final Member participant = createMember();
            teamRoleRepository.save(TeamRole.builder()
                    .teamId(study.getTeamId())
                    .memberId(participant.getId())
                    .teamRoleType(ROLE_팀원)
                    .build());
            participantCommandService.createParticipant(studyId, participant.getId(), member.getId());

            //when
            participantCommandService.deleteParticipant(studyId, participant.getId(), member.getId());

            //then
            assertThat(participantRepository.findByMemberId(participant.getId()).get(0).getIsDeleted()).isEqualTo(true);
        }

        @Test
        @DisplayName("[실패] 스터디장은 스터디장 본인을 탈퇴 시킬 수 없다.")
        public void deleteParticipant_스터디장은_스터디장_본인을_탈퇴_시킬_수_없다_실패() throws Exception {
            assertThatThrownBy(() -> {
                participantCommandService.deleteParticipant(study.getId(), member.getId(), member.getId());
            }).isInstanceOf(ParticipantException.class).hasMessage(CANNOT_DELETE_STUDY_LEADER_SELF.errorMessage());
        }

        @Test
        @DisplayName("[성공] 팀장은 스터디장을 탈퇴 시킬 수 있다.")
        public void deleteParticipant_팀장은_스터디장을_탈퇴_시킬_수_있다_성공() {
            participantRepository.save(Participant.builder().studyId(study.getId()).member(member).build());

            participantCommandService.deleteParticipant(study.getId(), member.getId(), teamLeader.getId());

            assertThat(participantRepository.findByMemberId(member.getId()).get(0).getIsDeleted()).isEqualTo(true);
        }

        @Test
        @DisplayName("[성공] 팀장이 스터디장을 탈퇴 시킬 경우 스터디장 권한은 팀장에게 위임된다.")
        public void deleteParticipant_팀장이_스터디장을_탈퇴_시킬_경우_스터디장_권한은_팀장에게_위임된다_성공() {
            participantRepository.save(Participant.builder().studyId(study.getId()).member(member).build());
            participantCommandService.deleteParticipant(study.getId(), member.getId(), teamLeader.getId());

            StudyRole afterStudyLeader = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(study.getId(),
                    teamLeader.getId()).orElseThrow();

            assertThat(afterStudyLeader.getMemberId()).isEqualTo(teamLeader.getId());
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자가 탈퇴 할 수 있다.")
        void withdrawParticipant_정상적으로_참여자가_탈퇴할_수_있다_성공() {
            //Given
            final Long studyId = study.getId();
            final Member participant = createMember();
            teamRoleRepository.save(TeamRole.builder()
                    .teamId(study.getTeamId())
                    .memberId(participant.getId())
                    .teamRoleType(ROLE_팀원)
                    .build());
            participantCommandService.createParticipant(studyId, participant.getId(), member.getId());

            //when
            participantCommandService.withdrawParticipant(studyId, participant.getId());

            //then
            assertThat(participantRepository.findByMemberId(participant.getId()).get(0).getIsDeleted()).isEqualTo(true);
        }
    }

    @Test
    @DisplayName("[성공] 팀장이 스터디원이고 팀장이 스터디장을 탈퇴시켰을 때 팀장의 직위는 스터디장으로 변경된다.")
    void deleteParticipant_팀장이_스터디원이고_팀장이_스터디장을_탈퇴시켰을_때_팀장의_직위는_스터디장으로_변경된다_성공() {
        participantCommandService.createParticipant(study.getId(), teamLeader.getId(), member.getId());
        final Optional<StudyRole> prevTeamLeaderStudyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(
                study.getId(), teamLeader.getId());
        assertThat(prevTeamLeaderStudyRole).isPresent();
        assertThat(prevTeamLeaderStudyRole.get().getStudyRoleType()).isEqualTo(ROLE_스터디원);

        participantCommandService.deleteParticipant(study.getId(), member.getId(), teamLeader.getId());
        final List<Participant> participants = participantRepository.findAllByStudyId(study.getId());
        final Optional<StudyRole> newTeamLeaderStudyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(
                study.getId(), teamLeader.getId());

        assertThat(participants.size()).isEqualTo(1);
        assertThat(newTeamLeaderStudyRole).isPresent();
        assertThat(newTeamLeaderStudyRole.get().getStudyRoleType()).isEqualTo(ROLE_스터디장);
    }
}
