package doore.study.api;

import static doore.study.StudyFixture.algorithm_study;
import static doore.team.TeamFixture.team;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;

import doore.helper.IntegrationTest;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class StudyControllerTest extends IntegrationTest {
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Nested
    @DisplayName("스터디 생성 테스트")
    class StudyCreateTest {
        @Test
        @DisplayName("정상적으로 스터디를 생성한다.")
        void 정상적으로_스터디를_생성한다_성공() throws Exception {
            final Team team = team();
            teamRepository.save(team);
            String url = "/teams/" + team.getId() + "/studies";
            final StudyCreateRequest request = new StudyCreateRequest("알고리즘", "알고리즘 스터디 입니다.",
                    LocalDate.parse("2020-01-01"), LocalDate.parse("2020-01-05"),
                    StudyStatus.IN_PROGRESS, false, 1L, null);

            callPostApi(url, request).andExpect(status().isCreated());
        }

        @ParameterizedTest
        @DisplayName("필수값이 입력되지 않은 경우 스터디 생성에 실패한다.")
        @CsvSource({
                ", 알고리즘 스터디입니다., 2022-01-01, 2022-01-05, IN_PROGRESS, false, 1",
                "알고리즘, , 2022-01-01, 2022-01-05, IN_PROGRESS, false, 1",
                "알고리즘, 알고리즘 스터디입니다., , 2022-01-05, IN_PROGRESS, false, 1",
                "알고리즘, 알고리즘 스터디입니다., 2022-01-01, 2022-01-05, , false, 1",
                "알고리즘, 알고리즘 스터디입니다., 2022-01-01, 2022-01-05, IN_PROGRESS, , 1",
                "알고리즘, 알고리즘 스터디입니다., 2022-01-01, 2022-01-05, IN_PROGRESS, false, "
        })
        void 필수값이_입력되지_않은_경우_스터디_생성에_실패한다_실패(String name, String description, String startDate, String endDate,
                                             String status, Boolean isDeleted, Long cropId)
                throws Exception {
            final StudyCreateRequest request = new StudyCreateRequest(
                    name, description, (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null,
                    LocalDate.parse(endDate),
                    (status != null && !status.isEmpty()) ? StudyStatus.valueOf(status) : null, isDeleted, cropId, null
            );

            callPostApi("/teams/1/studies", request).andExpect(status().isBadRequest());
        }
    }


    @Test
    @DisplayName("정상적으로 스터디를 삭제한다.")
    void 정상적으로_스터디를_삭제한다_성공() throws Exception {
        final Study study = mock(Study.class);
        String url = "/studies/" + study.getId();
        callDeleteApi(url).andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("정상적으로 스터디를 조회한다.")
    void 정상적으로_스터디를_조회한다_성공() throws Exception {
        final Study study = algorithm_study();
        studyRepository.save(study);
        String url = "/studies/" + study.getId();
        callGetApi(url).andExpect(status().isOk());
    }

    @Test
    @DisplayName("정상적으로 스터디를 수정한다.")
    void 정상적으로_스터디를_수정한다_성공() throws Exception {
        final Study study = algorithm_study();
        studyRepository.save(study);
        study.setName("스프링 스터디");
        String url = "/studies/" + study.getId();
        callPatchApi(url, study).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("정상적으로 스터디를 종료한다.")
    void 정상적으로_스터디를_종료한다_성공() throws Exception {
        final Study study = algorithm_study();
        studyRepository.save(study);
        String url = "/studies/" + study.getId() + "/termination";
        callPatchApi(url, study).andExpect(status().isNoContent());
    }

}
