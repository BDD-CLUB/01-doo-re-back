package doore.study.application;

import static doore.member.MemberFixture.회원;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyQueryServiceTest extends IntegrationTest {

    @Autowired
    StudyCommandService studyCommandService;
    @Autowired
    StudyQueryService studyQueryService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    StudyRepository studyRepository;

    @Nested
    @DisplayName("스터디 Query 테스트")
    class studyTest {
        @Test
        @DisplayName("[성공] 정상적으로 스터디 전체 정보를 조회할 수 있다.")
        void findStudyById_정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            Study study = algorithmStudy();
            studyRepository.save(study);
            assertEquals(study.getId(), studyQueryService.findStudyById(study.getId()).studyResponse().getId());
        }

        @Test
        @DisplayName("[성공] 정상적으로 스터디를 조회할 수 있다.")
        void getPersonalStudyDetail_정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            Study study = algorithmStudy();
            Long memberId = 1L;
            studyRepository.save(study);
            PersonalStudyDetailResponse personalStudyDetailResponse =
                    studyQueryService.getPersonalStudyDetail(study.getId(), memberId);
            assertAll(
                    () -> assertEquals(study.getId(), personalStudyDetailResponse.studyResponse().getId()),
                    () -> assertEquals(memberId, personalStudyDetailResponse.participantId())
            );
        }

        @Test
        @DisplayName("[실패] 존재하지 않는 스터디를 조회할 수 없다.")
        void findStudyById_존재하지_않는_스터디를_조회할_수_없다_실패() throws Exception {
            Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyQueryService.findStudyById(notExistingStudyId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }

    @Nested
    @DisplayName("참여자 Query 테스트")
    class participantTest {
        @Test
        @DisplayName("[성공] 참여자를 정상적으로 조회할 수 있다.")
        void findAllParticipants_참여자를_정상적으로_조회할_수_있다_성공() {
            //given
            Member member = 회원();
            memberRepository.save(member);
            Study study = algorithmStudy();
            studyRepository.save(study);
            studyCommandService.saveParticipant(study.getId(), member.getId());

            //when
            List<Participant> participants = studyQueryService.findAllParticipants(study.getId());

            //then
            assertAll(
                    () -> assertThat(participants).hasSize(1),
                    () -> assertEquals(member.getId(), participants.get(0).getMember().getId())
            );
        }
    }
}
