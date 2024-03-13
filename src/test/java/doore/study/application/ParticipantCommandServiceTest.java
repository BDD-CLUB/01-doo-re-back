package doore.study.application;

import static doore.member.MemberFixture.아마란스;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.study.StudyFixture.algorithmStudy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantCommandServiceTest extends IntegrationTest {

    @Autowired
    ParticipantCommandService participantCommandService;
    @Autowired
    ParticipantQueryService participantQueryService;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    MemberRepository memberRepository;

    @Nested
    @DisplayName("참여자 Command 테스트")
    class participantTest {

        Member member;
        Study study;

        @BeforeEach
        void setUp() {
            member = 아마란스();
            study = algorithmStudy();
            memberRepository.save(member);
            studyRepository.save(study);
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 추가할 수 있다.")
        void saveParticipant_정상적으로_참여자를_추가할_수_있다_성공() {
            //Given
            Long studyId = study.getId();
            Long memberId = member.getId();

            //when
            participantCommandService.saveParticipant(studyId, memberId);

            //then
            List<Participant> participants = participantQueryService.findAllParticipants(studyId);
            assertAll(
                    () -> assertThat(participants).hasSize(1),
                    () -> assertEquals(memberId, participants.get(0).getMember().getId())
            );
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자를 삭제할 수 있다.")
        void deleteParticipant_정상적으로_참여자를_삭제할_수_있다_성공() {
            //Given
            Long studyId = study.getId();
            Long memberId = member.getId();
            participantCommandService.saveParticipant(studyId, memberId);

            //when
            participantCommandService.deleteParticipant(studyId, memberId);
            List<Participant> participants = participantQueryService.findAllParticipants(studyId);

            //then
            assertThat(participants).hasSize(0);
        }

        @Test
        @DisplayName("[성공] 정상적으로 참여자가 탈퇴 할 수 있다.")
        void withdrawParticipant_정상적으로_참여자가_탈퇴할_수_있다_성공() {
            //Given
            Long studyId = study.getId();
            Long memberId = member.getId();
            participantCommandService.saveParticipant(studyId, memberId);

            //when
            participantCommandService.withdrawParticipant(studyId, memberId);
            List<Participant> participants = participantQueryService.findAllParticipants(studyId);

            //then
            assertThat(participants).hasSize(0);

        }
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 회원인 경우 실패한다.")
    void notExistMember_존재하지_않는_회원인_경우_실패한다_실패() {
        Study study = algorithmStudy();
        studyRepository.save(study);
        Long notExistingMemberId = 50L;

        assertThatThrownBy(() -> participantCommandService.saveParticipant(study.getId(), notExistingMemberId))
                .isInstanceOf(MemberException.class)
                .hasMessage(NOT_FOUND_MEMBER.errorMessage());
    }
}
