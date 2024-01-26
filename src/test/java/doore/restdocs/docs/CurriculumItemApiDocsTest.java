package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.restdocs.RestDocsTest;
import doore.study.api.CurriculumItemController;
import doore.study.application.CurriculumItemCommandService;
import doore.study.application.dto.request.CurriculumItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

@WebMvcTest(CurriculumItemController.class)
public class CurriculumItemApiDocsTest extends RestDocsTest {

    @MockBean
    protected CurriculumItemCommandService curriculumItemCommandService;

    @Test
    @DisplayName("커리큘럼이 정상 등록된다. [성공]")
    public void 커리큘림이_정상_등록된다_성공() throws Exception {
        Long studyId = 1L; // StudyFixture 생성 후 수정할 부분
        CurriculumItemRequest request = CurriculumItemRequest.builder().name("Spring Study").build();
        doNothing().when(curriculumItemCommandService).createCurriculum(any(CurriculumItemRequest.class), eq(studyId));

        RequestFieldsSnippet requestFields = requestFields(
                stringFieldWithPath("name", "커리큘럼 이름")
        );

        mockMvc.perform(post("/studies/{studyId}/curriculums", studyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("curriculum-create", requestFields));
    }

    // *****스터디 완료 되면 할 예정******
//    @Test
//    @DisplayName("커리큘럼이 정상 삭제된다. [성공]")
//    public void 커리큘럼이_정상_삭제된다_성공() throws Exception{
//
//    }

//    @Test
//    @DisplayName("커리큘럼이 정상 수정된다. [성공]")
//    public void 커리큘럼이_정상_수정된다_성공() throws Exception{
//
//    }

//    @Test
//    @DisplayName("커리큘럼의 완료와 미완료 상태가 변경된다. [성공]")
//    public void 커리큘럼의_완료와_미완료_상태가_변경된다_성공() throws Exception {
//
//    }
}
