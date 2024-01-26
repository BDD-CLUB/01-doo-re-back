package doore.restdocs.docs;

import static doore.study.StudyFixture.algorithm_study;
import static doore.study.StudyFixture.studyCreateRequest;
import static doore.study.StudyFixture.studyUpdateRequest;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import doore.restdocs.RestDocsTest;
import doore.study.api.StudyController;
import doore.study.application.StudyService;
import doore.study.application.dto.request.StudyCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(StudyController.class)
public class StudyApiDocsTest extends RestDocsTest {
    @MockBean
    protected StudyService studyService;

    @MockBean
    protected StudyRepository studyRepository;

    @Test
    @DisplayName("스터디를 생성한다.")
    public void 스터디를_생성한다() throws Exception {
        StudyCreateRequest request = studyCreateRequest();
        callPostApi("/teams/1/studies", request)
                .andExpect(status().isCreated())
                .andDo(document("study-create", requestFields(
                        stringFieldWithPath("name", "스터디 이름"),
                        stringFieldWithPath("description", "스터디 설명"),
                        stringFieldWithPath("startDate", "시작 날짜"),
                        stringFieldWithPath("endDate", "종료 날짜"),
                        stringFieldWithPath("status", "현재 상태"),
                        booleanFieldWithPath("isDeleted", "삭제 여부"),
                        numberFieldWithPath("cropId", "작물 id"),
                        arrayFieldWithPath("curriculumItems", "커리큘럼 아이템 리스트")
                )));
    }

    @Test
    @DisplayName("스터디를 조회한다.")
    public void 스터디를_조회한다() throws Exception {
        Study study = algorithm_study();
        studyRepository.save(study);
        callGetApi("/studies/1")
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("study-get"));
    }


    @Test
    @DisplayName("스터디를 삭제한다.")
    public void 스터디를_삭제한다() throws Exception {
        Study study = algorithm_study();
        studyRepository.save(study);
        callDeleteApi("/studies/1")
                .andExpect(status().isNoContent())
                .andDo(document("study-delete"));
    }

    @Test
    @DisplayName("스터디를 수정한다.")
    public void 스터디를_수정한다() throws Exception {
        Study study = algorithm_study();
        studyRepository.save(study);
        StudyUpdateRequest request = studyUpdateRequest();
        callPatchApi("/studies/1", request)
                .andExpect(status().isNoContent())
                .andDo(document("study-update", requestFields(
                        stringFieldWithPath("name", "스터디 이름"),
                        stringFieldWithPath("description", "스터디 설명"),
                        stringFieldWithPath("startDate", "시작 날짜"),
                        stringFieldWithPath("endDate", "종료 날짜"),
                        stringFieldWithPath("status", "현재 상태")
                )));
    }

    @Test
    @DisplayName("스터디를 종료한다.")
    public void 스터디를_종료한다() throws Exception {
        Study study = algorithm_study();
        studyRepository.save(study);
        callPatchApi("/studies/1/termination")
                .andExpect(status().isNoContent())
                .andDo(document("study-terminate"));
    }
}
