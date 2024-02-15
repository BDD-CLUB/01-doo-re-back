package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.restdocs.RestDocsTest;
import doore.study.api.CurriculumItemController;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.domain.CurriculumItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

@WebMvcTest(CurriculumItemController.class)
public class CurriculumItemApiDocsTest extends RestDocsTest {
    private CurriculumItemManageRequest request;

    @BeforeEach
    void setUp() {
        request = CurriculumItemManageRequest.builder()
                .curriculumItems(getCurriculumItems())
                .deletedCurriculumItems(getDeletedCurriculumItems())
                .build();
    }

    private List<CurriculumItem> getCurriculumItems() {
        List<CurriculumItem> curriculumItems = new ArrayList<>();
        curriculumItems.add(CurriculumItem.builder().id(1L).itemOrder(1).name("Change Spring Study").build());
        curriculumItems.add(CurriculumItem.builder().id(2L).itemOrder(4).name("CS Study").build());
        curriculumItems.add(CurriculumItem.builder().id(3L).itemOrder(2).name("Infra Study").build());
        curriculumItems.add(CurriculumItem.builder().id(4L).itemOrder(3).name("Algorithm Study").build());
        return curriculumItems;
    }

    private List<CurriculumItem> getDeletedCurriculumItems() {
        List<CurriculumItem> deletedCurriculumItems = new ArrayList<>();
        deletedCurriculumItems.add(CurriculumItem.builder().id(3L).itemOrder(2).name("Infra Study").build());
        return deletedCurriculumItems;
    }

    @Test
    @DisplayName("[성공] 커리큘럼 관리가 정상적으로 이루어진다.")
    public void manageCurriculum_커리큘럼_관리가_정상적으로_이루어진다() throws Exception {
        doNothing().when(curriculumItemCommandService).manageCurriculum(any(), any());

        mockMvc.perform(post("/studies/{studyId}/curriculums", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("curriculum-manage"));
    }

    @Test
    @DisplayName("[성공] 커리큘럼 상태가 정상적으로 변경된다.")
    public void checkCurriculum_커리큘럼_상태가_정상적으로_변경된다() throws Exception {
        doNothing().when(curriculumItemCommandService).checkCurriculum(any());

        mockMvc.perform(patch("/curriculums/{participantId}/check", 1))
                .andExpect(status().isNoContent())
                .andDo(document("curriculum-check"));
    }
}
