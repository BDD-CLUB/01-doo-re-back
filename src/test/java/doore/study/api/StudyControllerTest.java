package doore.study.api;

import static doore.member.MemberFixture.회원;
import static doore.study.StudyFixture.algorithmStudy;
import static doore.team.TeamFixture.team;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;

import doore.study.application.dto.request.StudyCreateRequest;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.study.domain.repository.StudyRepository;
import doore.team.domain.TeamRepository;
import doore.study.domain.Study;
import doore.team.domain.Team;
import doore.helper.IntegrationTest;
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
    private StudyRepository studyRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    MemberRepository memberRepository;
    Member member;
    Study study = mock(Study.class);

    @BeforeEach
    void setUp() {
        member = 회원();
        study = algorithmStudy();
        memberRepository.save(member);
        studyRepository.save(study);
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
        final Study study = algorithmStudy();
        studyRepository.save(study);
        String url = "/studies/" + study.getId();
        callDeleteApi(url).andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("정상적으로 스터디를 조회한다.")
    void 정상적으로_스터디를_조회한다_성공() throws Exception {
        final Study study = algorithmStudy();
        studyRepository.save(study);
        String url = "/studies/" + study.getId();
        callGetApi(url).andExpect(status().isOk());
    }

    @Test
    @DisplayName("정상적으로 스터디를 수정한다.")
    void 정상적으로_스터디를_수정한다_성공() throws Exception {
        final Study study = algorithmStudy();
        studyRepository.save(study);
        study.update("스프링 스터디", study.getDescription(), study.getStartDate(), study.getEndDate(), study.getStatus());
        String url = "/studies/" + study.getId();
        callPutApi(url, study).andExpect(status().isOk());
    }

    @Test
    @DisplayName("정상적으로 스터디의 상태를 변경한다.")
    void 정상적으로_스터디의_상태를_변경한다_성공() throws Exception {
        final Study study = algorithmStudy();
        studyRepository.save(study);
        String url = "/studies/" + study.getId() + "/status?status=IN_PROGRESS";
        callPatchApi(url, study).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("정상적으로 스터디를 종료한다.")
    void 정상적으로_스터디를_종료한다_성공() throws Exception {
        final Study study = algorithmStudy();
        studyRepository.save(study);
        String url = "/studies/" + study.getId() + "/termination";
        callPatchApi(url, study).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[성공] 정상적으로 참여자를 추가할 수 있다.")
    void saveParticipant_정상적으로_참여자를_추가할_수_있다_성공() throws Exception {
        String url = "/studies/" + study.getId() + "/members/" + member.getId();
        callPostApi(url).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("[성공] 정상적으로 참여자를 삭제할 수 있다.")
    void deleteParticipant_정상적으로_참여자를_삭제할_수_있다_성공() throws Exception {
        String url = "/studies/" + study.getId() + "/members/" + member.getId();
        callDeleteApi(url).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[성공] 정상적으로 참여자를 조회할 수 있다.")
    void getParticipant_정상적으로_참여자를_조회할_수_있다_성공() throws Exception {
        String url = "/studies/" + study.getId() + "/members";
        callGetApi(url).andExpect(status().isOk());
    }
}
