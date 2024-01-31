package doore.restdocs.docs;

import static doore.member.MemberFixture.회원;
import static doore.study.StudyFixture.algorithmStudy;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.ParticipantRepository;
import doore.study.application.StudyCommandService;
import doore.study.application.StudyQueryService;
import doore.study.application.dto.request.CurriculumItemsRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import doore.restdocs.RestDocsTest;
import doore.study.api.StudyController;
import doore.study.application.dto.request.StudyCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;

@WebMvcTest(StudyController.class)
public class StudyApiDocsTest extends RestDocsTest {
    @MockBean
    protected StudyCommandService studyCommandService;
    @MockBean
    protected StudyQueryService studyQueryService;
    @MockBean
    protected StudyRepository studyRepository;
    @MockBean
    protected MemberRepository memberRepository;
    @MockBean
    protected ParticipantRepository participantRepository;


    @Test
    @DisplayName("스터디를 생성한다.")
    public void 스터디를_생성한다() throws Exception {
        StudyCreateRequest request = StudyCreateRequest.builder()
                .name("알고리즘")
                .description("알고리즘 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .cropId(1L)
                .curriculumItems(new ArrayList<CurriculumItemsRequest>())
                .build();
        callPostApi("/teams/1/studies", request)
                .andExpect(status().isCreated())
                .andDo(document("study-create", requestFields(
                        stringFieldWithPath("name", "스터디 이름"),
                        stringFieldWithPath("description", "스터디 설명"),
                        stringFieldWithPath("startDate", "시작 날짜"),
                        stringFieldWithPath("endDate", "종료 날짜"),
                        numberFieldWithPath("cropId", "작물 id"),
                        arrayFieldWithPath("curriculumItems", "커리큘럼 아이템 리스트")
                )));
    }

    @Test
    @DisplayName("스터디를 조회한다.")
    public void 스터디를_조회한다() throws Exception {
        Study study = mock(Study.class);
        studyRepository.save(study);
        callGetApi("/studies/1")
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("study-get"));
    }


    @Test
    @DisplayName("스터디를 삭제한다.")
    public void 스터디를_삭제한다() throws Exception {
        Study study = mock(Study.class);
        studyRepository.save(study);
        callDeleteApi("/studies/1")
                .andExpect(status().isNoContent())
                .andDo(document("study-delete"));
    }

    @Test
    @DisplayName("스터디를 수정한다.")
    public void 스터디를_수정한다() throws Exception {
        Study study = mock(Study.class);
        studyRepository.save(study);
        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .name("스프링")
                .description("스프링 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .status(StudyStatus.IN_PROGRESS)
                .build();
        callPutApi("/studies/1", request)
                .andExpect(status().isOk())
                .andDo(document("study-update", requestFields(
                        stringFieldWithPath("name", "스터디 이름"),
                        stringFieldWithPath("description", "스터디 설명"),
                        stringFieldWithPath("startDate", "시작 날짜"),
                        stringFieldWithPath("endDate", "종료 날짜"),
                        stringFieldWithPath("status", "현재 상태")
                )));
    }

    @Test
    @DisplayName("스터디의 상태를 수정한다.")
    public void 스터디의_상태를_수정한다() throws Exception {
        Study study = mock(Study.class);
        studyRepository.save(study);
        callPatchApi("/studies/1/status?status=IN_PROGRESS")
                .andExpect(status().isNoContent())
                .andDo(document("study-change-status"));
    }

    @Test
    @DisplayName("스터디를 종료한다.")
    public void 스터디를_종료한다() throws Exception {
        Study study = mock(Study.class);
        studyRepository.save(study);
        callPatchApi("/studies/1/termination")
                .andExpect(status().isNoContent())
                .andDo(document("study-terminate"));
    }

    @Test
    @DisplayName("참여자를 추가한다.")
    void 참여자를_추가한다_성공() throws Exception {
        String url = "/studies/1/members/1";
        callPostApi(url).andExpect(status().isCreated())
                .andDo(document("participant-save"));
    }

    @Test
    @DisplayName("참여자를 삭제한다.")
    void 참여자를_삭제한다_성공() throws Exception {
        String url = "/studies/1/members/1";
        callDeleteApi(url).andExpect(status().isNoContent())
                .andDo(document("participant-delete"));
    }

    @Test
    @DisplayName("참여자가 탈퇴한다.")
    void 참여자가_탈퇴한다_성공() throws Exception {
        String url = "/studies/1/members";
        MockHttpSession session = new MockHttpSession();
        callDeleteApi(url, session).andExpect(status().isNoContent())
                .andDo(document("participant-withdraw"));
    }

    @Test
    @DisplayName("참여자를 조회한다.")
    void 참여자를_조회한다_성공() throws Exception {
        Member member = mock(Member.class);

        String url = "/studies/1/members";
        callGetApi(url).andExpect(status().isOk())
                .andDo(document("participant-get"));
    }
}
