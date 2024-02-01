package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.restdocs.RestDocsTest;
import doore.study.api.CurriculumItemController;
import doore.study.application.CurriculumItemCommandService;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

@WebMvcTest(CurriculumItemController.class)
public class CurriculumItemApiDocsTest extends RestDocsTest {

    @MockBean
    protected CurriculumItemCommandService curriculumItemCommandService;
    @MockBean
    protected StudyRepository studyRepository;
    @MockBean
    protected CurriculumItemRepository curriculumItemRepository;

    private Long validStudyId;
    private Long validCurriculumItemId;

    @BeforeEach
    void setUp() {
        validStudyId = 1L;
        validCurriculumItemId = 1L;
    }

    @Test
    @DisplayName("[성공] 커리큘럼이 정상 등록된다.")
    public void createCurriculum_커리큘림이_정상_등록된다_성공() throws Exception {
        CurriculumItemRequest request = new CurriculumItemRequest("Spring Study");
        doNothing().when(curriculumItemCommandService)
                .createCurriculum(any(CurriculumItemRequest.class), eq(validStudyId));

        RequestFieldsSnippet requestFields = requestFields(
                stringFieldWithPath("name", "커리큘럼 이름")
        );

        String url = "/studies/" + validStudyId + "/curriculums";
        callPostApi(url, request)
                .andExpect(status().isCreated())
                .andDo(document("curriculum-create", requestFields));
    }

    @Test
    @DisplayName("[성공] 커리큘럼이 정상 삭제된다.")
    public void deleteCurriculum_커리큘럼이_정상_삭제된다_성공() throws Exception {
        doNothing().when(curriculumItemCommandService).deleteCurriculum(eq(validStudyId), eq(validCurriculumItemId));

        String url = "/studies/" + validStudyId + "/curriculums/" + validCurriculumItemId;
        callDeleteApi(url)
                .andExpect(status().isNoContent())
                .andDo(document("curriculum-delete"));
    }

    @Test
    @DisplayName("[성공] 커리큘럼이 정상 수정된다.")
    public void updateCurriculum_커리큘럼이_정상_수정된다_성공() throws Exception {
        CurriculumItemRequest request = new CurriculumItemRequest("Change Spring Study");
        doNothing().when(curriculumItemCommandService)
                .updateCurriculum(eq(validCurriculumItemId), eq(validStudyId), any(CurriculumItemRequest.class));

        RequestFieldsSnippet requestFields = requestFields(
                stringFieldWithPath("name", "커리큘럼 이름")
        );

        String url = "/studies/" + validStudyId + "/curriculums/" + validCurriculumItemId;
        callPatchApi(url, request)
                .andExpect(status().isNoContent())
                .andDo(document("curriculum-update", requestFields));
    }

}
