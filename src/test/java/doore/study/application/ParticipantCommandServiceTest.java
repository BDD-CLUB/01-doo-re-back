package doore.study.application;

import static doore.member.MemberFixture.createMember;
import static doore.member.MemberFixture.아마란스;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.study.StudyFixture.algorithmStudy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.StudyRole;
import doore.member.domain.StudyRoleType;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
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

    private Member member;
    private Study study;
    private StudyRole studyRole;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(아마란스());
        study = studyRepository.save(algorithmStudy());
        studyRole = studyRoleRepository.save(StudyRole.builder()
                .studyRoleType(StudyRoleType.ROLE_스터디장)
                .studyId(study.getId())
                .memberId(member.getId())
                .build());
    }

    @Nested
    @DisplayName("참여자 Command 테스트")
    class participantTest {

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 추가할 수 있다.")
        void saveParticipant_정상적으로_참여자를_추가할_수_있다_성공() {
            //Given
            final Long studyId = study.getId();
            final Long memberId = member.getId();

            //when
            participantCommandService.saveParticipant(studyId, memberId, member.getId());

            //then
            final List<Participant> participants = participantQueryService.findAllParticipants(studyId, memberId);
            assertAll(
                    () -> assertThat(participants).hasSize(1),
                    () -> assertEquals(memberId, participants.get(0).getMember().getId())
            );
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 삭제할 수 있다.")
        void deleteParticipant_정상적으로_참여자를_삭제할_수_있다_성공() {
            //Given
            final Long studyId = study.getId();
            final Member participant = createMember();
            participantCommandService.saveParticipant(studyId, participant.getId(), member.getId());

            //when
            participantCommandService.deleteParticipant(studyId, participant.getId(), member.getId());
            final List<Participant> participants = participantQueryService.findAllParticipants(studyId,
                    participant.getId());

            //then
            assertThat(participants).hasSize(0);
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자가 탈퇴 할 수 있다.")
        void withdrawParticipant_정상적으로_참여자가_탈퇴할_수_있다_성공() {
            //Given
            final Long studyId = study.getId();
            final Member participant = createMember();

            participantCommandService.saveParticipant(studyId, participant.getId(), member.getId());

            //when
            participantCommandService.withdrawParticipant(studyId, participant.getId(), participant.getId());
            final List<Participant> participants = participantQueryService.findAllParticipants(studyId,
                    participant.getId());

            //then
            assertThat(participants).hasSize(0);
        }
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 회원인 경우 실패한다.")
    void notExistMember_존재하지_않는_회원인_경우_실패한다_실패() {
        final Long notExistingMemberId = 50L;

        assertThatThrownBy(
                () -> participantCommandService.saveParticipant(study.getId(), notExistingMemberId, member.getId()))
                .isInstanceOf(MemberException.class)
                .hasMessage(NOT_FOUND_MEMBER.errorMessage());
    }
}
