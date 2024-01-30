package doore.study.application;

import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.domain.StudyStatus.ENDED;
import static doore.study.exception.StudyExceptionType.*;
import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static doore.study.domain.StudyStatus.UPCOMING;

import doore.helper.IntegrationTest;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.exception.StudyException;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyServiceTest extends IntegrationTest {
    @Autowired
    StudyCommandService studyCommandService;
    @Autowired
    StudyQueryService studyQueryService;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    TeamRepository teamRepository;

    @Nested
    @DisplayName("스터디 생성 테스트")
    class StudyCreateTest {
        StudyCreateRequest studyCreateRequest;
        Team team;

        @BeforeEach
        void setUp() {
            studyCreateRequest = StudyCreateRequest.builder()
                    .name("자바 스터디")
                    .description("자바 스터디 입니다")
                    .startDate(LocalDate.parse("2020-02-02"))
                    .endDate(null)
                    .cropId(1L)
                    .curriculumItems(null)
                    .build();
            team = team();
            teamRepository.save(team);
        }

        @Test
        @DisplayName("정상적으로 스터디를 생성할 수 있다.")
        void 정상적으로_스터디를_생성할_수_있다_성공() throws Exception {
            studyCommandService.createStudy(studyCreateRequest, team.getId());
            List<Study> studies = studyRepository.findAll();
            assertThat(studies).hasSize(1);
            Study study = studies.get(0);
            assertEquals(study.getName(), studyCreateRequest.name());
            assertEquals(study.getDescription(), studyCreateRequest.description());
            assertEquals(study.getStartDate(), studyCreateRequest.startDate());
            assertEquals(study.getEndDate(), studyCreateRequest.endDate());
        }

        @Test
        @DisplayName("스터디의 status와 isDeleted가 초기값으로 초기화 된다.")
        void 스터디의_status와_isDeleted가_초기값으로_초기화_된다_성공() throws Exception {
            studyCommandService.createStudy(studyCreateRequest, team.getId());
            List<Study> studies = studyRepository.findAll();
            Study study = studies.get(0);
            assertAll(
                    () -> assertEquals(UPCOMING, study.getStatus()),
                    () -> assertEquals(false, study.getIsDeleted())
            );

        }

        @Test
        @DisplayName("스터디 생성시 curriculum을 작성하지 않으면 빈 리스트로 생성된다.")
        void 스터디_생성시_curriculum을_작성하지_않으면_빈_리스트로_생성된다_성공() throws Exception {
            studyCommandService.createStudy(studyCreateRequest, team.getId());
            List<Study> studies = studyRepository.findAll();
            Study study = studies.get(0);
            assertEquals(Collections.emptyList(), study.getCurriculumItems());
        }

        @Test
        @DisplayName("스터디 종료일이 스터디 시작일보다 앞설 수 없다.")
        void 스터디_종료일이_스터디_시작일보다_앞설_수_없다_실패() throws Exception {
            StudyCreateRequest wrongRequest = StudyCreateRequest.builder()
                    .name("스터디")
                    .description("스터디입니다.")
                    .startDate(LocalDate.parse("2020-02-02"))
                    .endDate(LocalDate.parse("2000-02-02"))
                    .cropId(1L)
                    .curriculumItems(null)
                    .build();
            assertThatThrownBy(() -> studyCommandService.createStudy(wrongRequest, team.getId()))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(INVALID_ENDDATE.errorMessage());
        }
    }

    @Test
    @DisplayName("정상적으로 스터디를 삭제할 수 있다.")
    void 정상적으로_스터디를_삭제할_수_있다() throws Exception {
        Study study = algorithmStudy();
        studyRepository.save(study);
        studyCommandService.deleteStudy(study.getId());
        List<Study> studies = studyRepository.findAll();
        assertThat(studies).hasSize(0);
    }

    @Nested
    @DisplayName("스터디 조회 테스트")
    class StudyGetTest {
        @Test
        @DisplayName("정상적으로 스터디를 조회할 수 있다.")
        void 정상적으로_스터디를_조회할_수_있다_성공() throws Exception {
            Study study = algorithmStudy();
            studyRepository.save(study);
            assertEquals(study.getId(), studyQueryService.findStudyById(study.getId()).id());
        }

        @Test
        @DisplayName("존재하지 않는 스터디를 조회할 수 없다.")
        void 존재하지_않는_스터디를_조회할_수_없다_실패() throws Exception {
            Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyQueryService.findStudyById(notExistingStudyId))
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
            final Study study = algorithmStudy();
            studyRepository.save(study);
            studyCommandService.terminateStudy(study.getId());

            assertEquals(ENDED, study.getStatus());
        }
    }

    @Nested
    @DisplayName("스터디 수정 테스트")
    class StudyupdateTest {
        final StudyUpdateRequest request = StudyUpdateRequest.builder()
                .name("스프링")
                .description("스프링 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .status(StudyStatus.IN_PROGRESS)
                .build();

        @Test
        @DisplayName("정상적으로_스터디를_수정할_수_있다.")
        void 정상적으로_스터디를_수정할_수_있다_성공() throws Exception {
            final Study study = algorithmStudy();
            studyRepository.save(study);
            studyCommandService.updateStudy(request, study.getId());
            assertEquals(study.getName(), request.name());
        }

        @Test
        @DisplayName("존재하지_않는_스터디를_수정할_수_없다.")
        void 존재하지_않는_스터디를_수정할_수_없다_실패() throws Exception {
            Long notExistingStudyId = 0L;
            assertThatThrownBy(() -> studyCommandService.updateStudy(request, notExistingStudyId))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STUDY.errorMessage());
        }
    }

    @Nested
    @DisplayName("스터디 상태 수정 테스트")
    class StudyChangeStatusTest {
        @Test
        @DisplayName("존재하지 않는 상태로 변경할 수 없다.")
        void 존재하지_않는_상태로_변경할_수_없다_실패() throws Exception {
            final Study study = algorithmStudy();
            studyRepository.save(study);
            assertThatThrownBy(() -> studyCommandService.changeStudyStatus("NOT_EXISTING_STATUS", study.getId()))
                    .isInstanceOf(StudyException.class)
                    .hasMessage(NOT_FOUND_STATUS.errorMessage());
        }
    }
}
