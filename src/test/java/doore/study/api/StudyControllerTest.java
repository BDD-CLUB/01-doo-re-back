package doore.study.api;

import static doore.member.MemberFixture.아마란스;
import static doore.team.TeamFixture.team;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.study.StudyFixture;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.helper.IntegrationTest;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.study.domain.Study;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

public class StudyControllerTest extends IntegrationTest {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    MemberRepository memberRepository;
    Member member;
    Study study = mock(Study.class);

    @BeforeEach
    void setUp() {
        member = 아마란스();
        study = StudyFixture.builder().studyBuild();
        memberRepository.save(member);
    }

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
                    LocalDate.parse("2020-01-01"), LocalDate.parse("2020-01-05"), 1L, null);
            callPostApi(url, request).andExpect(status().isCreated());
        }

        @ParameterizedTest
        @DisplayName("필수값이 입력되지 않은 경우 스터디 생성에 실패한다.")
        @CsvSource({
                ", 알고리즘 스터디입니다., 2022-01-01, 2022-01-05, 1",
                "알고리즘, , 2022-01-01, 2022-01-05, 1",
                "알고리즘, 알고리즘 스터디입니다., , 2022-01-05, 1",
                "알고리즘, 알고리즘 스터디입니다., 2022-01-01, 2022-01-05, "
        })
        void 필수값이_입력되지_않은_경우_스터디_생성에_실패한다_실패(String name, String description, String startDate, String endDate,
                                             Long cropId) throws Exception {
            final StudyCreateRequest request = new StudyCreateRequest(name, description,
                    (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null,
                    LocalDate.parse(endDate), cropId, null);

            callPostApi("/teams/1/studies", request).andExpect(status().isBadRequest());
        }

    }


    @Test
    @DisplayName("정상적으로 스터디를 삭제한다.")
    void 정상적으로_스터디를_삭제한다_성공() throws Exception {
        String url = "/studies/" + study.getId();
        callDeleteApi(url).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("정상적으로 스터디를 수정한다.")
    void 정상적으로_스터디를_수정한다_성공() throws Exception {
        study.update("스프링 스터디", study.getDescription(), study.getStartDate(), study.getEndDate(), study.getStatus());
        String url = "/studies/" + study.getId();
        callPutApi(url, study).andExpect(status().isOk());
    }

    @Test
    @DisplayName("정상적으로 스터디의 상태를 변경한다.")
    void 정상적으로_스터디의_상태를_변경한다_성공() throws Exception {
        String url = "/studies/" + study.getId() + "/status?status=IN_PROGRESS";
        callPatchApi(url, study).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("정상적으로 스터디를 종료한다.")
    void 정상적으로_스터디를_종료한다_성공() throws Exception {
        String url = "/studies/" + study.getId() + "/termination";
        callPatchApi(url, study).andExpect(status().isNoContent());
    }
}
