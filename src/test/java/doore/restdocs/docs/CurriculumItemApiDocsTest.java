package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.restdocs.RestDocsTest;
import doore.study.application.dto.request.CurriculumItemManageDetailRequest;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class CurriculumItemApiDocsTest extends RestDocsTest {
    private CurriculumItemManageRequest request;

    @BeforeEach
    void setUp() {
        request = CurriculumItemManageRequest.builder()
                .curriculumItems(getCurriculumItems())
                .deletedCurriculumItems(getDeletedCurriculumItems())
                .build();
    }

    private List<CurriculumItemManageDetailRequest> getCurriculumItems() {
        List<CurriculumItemManageDetailRequest> curriculumItems = new ArrayList<>();
        curriculumItems.add(CurriculumItemManageDetailRequest.builder().id(1L).itemOrder(1).name("Change Spring Study").build());
        curriculumItems.add(CurriculumItemManageDetailRequest.builder().id(2L).itemOrder(4).name("CS Study").build());
        curriculumItems.add(CurriculumItemManageDetailRequest.builder().id(3L).itemOrder(2).name("Infra Study").build());
        curriculumItems.add(CurriculumItemManageDetailRequest.builder().id(4L).itemOrder(3).name("Algorithm Study").build());
        return curriculumItems;
    }

    private List<CurriculumItemManageDetailRequest> getDeletedCurriculumItems() {
        List<CurriculumItemManageDetailRequest> deletedCurriculumItems = new ArrayList<>();
        deletedCurriculumItems.add(CurriculumItemManageDetailRequest.builder().id(3L).itemOrder(2).name("Infra Study").build());
        return deletedCurriculumItems;
    }

    @Test
    @DisplayName("[성공] 커리큘럼 관리가 정상적으로 이루어진다.")
    public void manageCurriculum_커리큘럼_관리가_정상적으로_이루어진다() throws Exception {
        doNothing().when(curriculumItemCommandService).manageCurriculum(any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/curriculums", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("curriculum-manage", pathParameters(
                                parameterWithName("studyId").description("스터디 id")),
                        requestFields(
                                fieldWithPath("curriculumItems").description("커리큘럼 아이템 리스트"),
                                fieldWithPath("curriculumItems[].id").description("커리큘럼 아이템 ID"),
                                fieldWithPath("curriculumItems[].itemOrder").description("커리큘럼 아이템 순서"),
                                fieldWithPath("curriculumItems[].name").description("커리큘럼 아이템 이름"),
                                fieldWithPath("deletedCurriculumItems").description("삭제된 커리큘럼 아이템 리스트"),
                                fieldWithPath("deletedCurriculumItems[].id").description("삭제된 커리큘럼 아이템 ID"),
                                fieldWithPath("deletedCurriculumItems[].itemOrder").description("삭제된 커리큘럼 아이템 순서"),
                                fieldWithPath("deletedCurriculumItems[].name").description("삭제된 커리큘럼 아이템 이름")
                        )
                ));
    }

    @Test
    @DisplayName("[성공] 커리큘럼 상태가 정상적으로 변경된다.")
    public void checkCurriculum_커리큘럼_상태가_정상적으로_변경된다() throws Exception {
        doNothing().when(curriculumItemCommandService).checkCurriculum(any(), any());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/curriculums/{curriculumId}/{participantId}/check", 1, 1))
                .andExpect(status().isNoContent())
                .andDo(document("curriculum-check", pathParameters(
                        parameterWithName("curriculumId").description("커리큘럼 id"),
                        parameterWithName("participantId").description("참여자 id")
                )));
    }
}
