package doore.study.application;

import static doore.study.StudyFixture.algorithm_study;
import static doore.study.StudyFixture.studyCreateRequest;
import static doore.study.StudyFixture.studyUpdateRequest;
import static doore.study.domain.StudyStatus.ENDED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.study.exception.StudyExceptionType.TERMINATED_STUDY;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.IntegrationTest;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class StudyServiceTest extends IntegrationTest {
    @Autowired
    StudyService studyService;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    TeamRepository teamRepository;

    @Test
    @DisplayName("정상적으로 스터디를 생성할 수 있다.")
    void 정상적으로_스터디를_생성할_수_있다_성공() throws Exception {
        StudyCreateRequest studyCreateRequest = studyCreateRequest();
        Team team = team();
        teamRepository.save(team);
        studyService.createStudy(studyCreateRequest, team.getId());
        List<Study> studies = studyRepository.findAll();
        assertThat(studies).hasSize(1);
        Study study = studies.get(0);
        assertEquals(study.getName(), studyCreateRequest.name());
        assertEquals(study.getDescription(), studyCreateRequest.description());
        assertEquals(study.getStartDate(), studyCreateRequest.startDate());
        assertEquals(study.getEndDate(), studyCreateRequest.endDate());
    }

    @Test
    @DisplayName("정상적으로 스터디를 삭제할 수 있다.")
    void 정상적으로_스터디를_삭제할_수_있다() throws Exception {
        Study study = algorithm_study();
        studyRepository.save(study);
        studyService.deleteStudy(study.getId());
        List<Study> studies = studyRepository.findAll();
        assertThat(studies).hasSize(0);
    }

    @Nested
    @DisplayName("스터디 조회 테스트")
    class StudyGetTest {
        @Test
        @DisplayName("정상적으로 스터디를 조회할 수 있다.")
        void 정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            Study study = algorithm_study();
            studyRepository.save(study);
            assertEquals(study.getId(), studyService.findStudyById(study.getId()).id());
        }

        @Test
        @DisplayName("존재하지 않는 스터디를 조회할 수 없다.")
        void 존재하지_않는_스터디를_조회할_수_없다_실패() throws Exception {
            Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyService.findStudyById(notExistingStudyId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }

    @Nested
    @DisplayName("스터디 종료 테스트")
    class StudyTerminatetest {
        @Test
        @DisplayName("정상적으로 스터디를 종료할 수 있다.")
        void 정상적으로_스터디를_종료할_수_있다_성공() throws Exception {
            final Study study = algorithm_study();
            studyRepository.save(study);
            studyService.terminateStudy(study.getId());

            assertEquals(ENDED, study.getStatus());
        }

        @Test
        @DisplayName("종료된 스터디를 종료할 수 없다.")
        void 종료된_스터디를_종료할_수_없다_실패() throws Exception {
            final Study study = algorithm_study();
            study.setStatus(ENDED);
            studyRepository.save(study);

            assertThatThrownBy(() -> studyService.terminateStudy(study.getId()))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(TERMINATED_STUDY.errorMessage());

        }
    }

    @Nested
    @DisplayName("스터디 수정 테스트")
    class StudyupdateTest {
        final StudyUpdateRequest request = studyUpdateRequest();

        @Test
        @DisplayName("정상적으로_스터디를_수정할_수_있다.")
        void 정상적으로_스터디를_수정할_수_있다_성공() throws Exception {
            final Study study = algorithm_study();
            studyRepository.save(study);
            studyService.updateStudy(request, study.getId());
            assertEquals(study.getName(), request.name());
        }

        @Test
        @DisplayName("존재하지_않는_스터디를_수정할_수_없다.")
        void 존재하지_않는_스터디를_수정할_수_없다_실패() throws Exception {
            Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyService.updateStudy(request, notExistingStudyId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }
}
