package doore.study.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;

import doore.helper.IntegrationTest;
import doore.study.application.dto.request.CurriculumItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CurriculumItemControllerTest extends IntegrationTest {

    @Test
    @DisplayName("커리큘림이 정상 등록된다. [성공]")
    public void 커리큘림이_정상_등록된다_성공() throws Exception {
        CurriculumItemRequest request = CurriculumItemRequest.builder().name("Spring MVC").build();

        callPostApi("/curriculums", request).andExpect(status().isCreated());
    }

    // ******스터디 관련 코드 문제******
//    @Test
//    @DisplayName("커리큘럼이 정상 삭제된다. [성공]")
//    public void 커리큘럼이_정상_삭제된다_성공() throws Exception {
//        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();
//
//        mockMvc.perform(delete("/curriculums/{curriculumsId}", curriculumItem.getId()))
//                .andExpect(status().isNoContent());
//    }


    @Test
    @DisplayName("필수 정보를 입력하지 않았다면 커리큘럼 등록은 실패한다. [실패]")
    public void 필수_정보를_입력하지_않았다면_커리큘럼_등록과_수정은_실패한다() throws Exception {
        CurriculumItemRequest request = CurriculumItemRequest.builder().name(null).build();

        callPostApi("/curriculums", request).andExpect(status().isBadRequest());
    }

}
