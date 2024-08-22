package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.restdocs.RestDocsTest;
import doore.study.application.dto.request.CurriculumItemManageDetailRequest;
import doore.study.application.dto.request.CurriculumItemManageRequest;
import doore.study.application.dto.response.CurriculumItemResponse;
import doore.study.application.dto.response.ParticipantCurriculumItemResponse;
import doore.study.application.dto.response.PersonalCurriculumItemResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class CurriculumItemApiDocsTest extends RestDocsTest {
    private CurriculumItemManageRequest request;
    private String accessToken;
    private ParticipantCurriculumItemResponse participantCurriculumItemResponse;
    private ParticipantCurriculumItemResponse otherParticipantCurriculumItemResponse;
    private CurriculumItemResponse curriculumItemResponse;

    @BeforeEach
    void setUp() {
        request = CurriculumItemManageRequest.builder()
                .curriculumItems(getCurriculumItems())
                .deletedCurriculumItems(getDeletedCurriculumItems())
                .build();
        accessToken = "mocked-access-token";
        when(jwtTokenGenerator.generateToken(any(String.class))).thenReturn(accessToken);

        participantCurriculumItemResponse = new ParticipantCurriculumItemResponse(1L, 1L, false);
        otherParticipantCurriculumItemResponse = new ParticipantCurriculumItemResponse(2L, 1L, true);
        curriculumItemResponse = new CurriculumItemResponse(1L, "chapter1. greedy", 0, false,
                List.of(participantCurriculumItemResponse));
    }

    private List<CurriculumItemManageDetailRequest> getCurriculumItems() {
        final List<CurriculumItemManageDetailRequest> curriculumItems = new ArrayList<>();
        curriculumItems.add(
                CurriculumItemManageDetailRequest.builder().id(1L).itemOrder(1).name("Change Spring Study").build());
        curriculumItems.add(CurriculumItemManageDetailRequest.builder().id(2L).itemOrder(4).name("CS Study").build());
        curriculumItems.add(
                CurriculumItemManageDetailRequest.builder().id(3L).itemOrder(2).name("Infra Study").build());
        curriculumItems.add(
                CurriculumItemManageDetailRequest.builder().id(4L).itemOrder(3).name("Algorithm Study").build());
        return curriculumItems;
    }

    private List<CurriculumItemManageDetailRequest> getDeletedCurriculumItems() {
        final List<CurriculumItemManageDetailRequest> deletedCurriculumItems = new ArrayList<>();
        deletedCurriculumItems.add(
                CurriculumItemManageDetailRequest.builder().id(3L).itemOrder(2).name("Infra Study").build());
        return deletedCurriculumItems;
    }

    @Test
    @DisplayName("[성공] 커리큘럼 관리가 정상적으로 이루어진다.")
    public void manageCurriculum_커리큘럼_관리가_정상적으로_이루어진다() throws Exception {
        doNothing().when(curriculumItemCommandService).manageCurriculum(any(), any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/curriculums", 1)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isCreated())
                .andDo(document("curriculum-manage", pathParameters(
                                parameterWithName("studyId").description("스터디 id")),
                        requestFields(
                                fieldWithPath("curriculumItems").description("커리큘럼 아이템 리스트"),
                                fieldWithPath("curriculumItems[].id").description("커리큘럼 아이템 ID (새로 생성된 경우 null)"),
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
        doNothing().when(curriculumItemCommandService).checkCurriculum(any(), any(), any());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/curriculums/{curriculumId}/{participantId}/check", 1, 1)
                                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isNoContent())
                .andDo(document("curriculum-check", pathParameters(
                        parameterWithName("curriculumId").description("커리큘럼 id"),
                        parameterWithName("participantId").description("참여자 id")
                )));
    }

    @Test
    @DisplayName("[성공] 스터디의 커리큘럼을 정상적으로 조회할 수 있다.")
    public void getCurriculums_스터디의_커리큘럼을_정상적으로_조회할_수_있다() throws Exception {
        //when
        when(curriculumItemQueryService.getCurriculums(any())).thenReturn(List.of(curriculumItemResponse));

        //then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/studies/{studyId}/curriculums/all", 1))
                .andExpect(status().isOk())
                .andDo(document("curriculum-get", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
                ));
    }

    @Test
    @DisplayName("[성공] 스터디의 특정 회원의 커리큘럼을 정상적으로 조회할 수 있다.")
    public void getMyCurriculum_스터디의_특정_회원의_커리큘럼을_정상적으로_조회할_수_있다() throws Exception {
        //given
        final CurriculumItemResponse otherCurriculumItemResponse = new CurriculumItemResponse(
                1L, "chapter2. DFS", 0, false,
                List.of(otherParticipantCurriculumItemResponse));

        final PersonalCurriculumItemResponse personalCurriculumItemResponse = PersonalCurriculumItemResponse.builder()
                .id(participantCurriculumItemResponse.id())
                .participantId(participantCurriculumItemResponse.participantId())
                .itemOrder(curriculumItemResponse.itemOrder())
                .isChecked(participantCurriculumItemResponse.isChecked())
                .name(curriculumItemResponse.name())
                .build();
        final PersonalCurriculumItemResponse otherPersonalCurriculumItemResponse = PersonalCurriculumItemResponse.builder()
                .id(otherParticipantCurriculumItemResponse.id())
                .participantId(otherParticipantCurriculumItemResponse.participantId())
                .itemOrder(otherCurriculumItemResponse.itemOrder())
                .isChecked(otherParticipantCurriculumItemResponse.isChecked())
                .name(otherCurriculumItemResponse.name())
                .build();

        //when
        when(curriculumItemQueryService.getMyCurriculum(any(), any())).thenReturn(
                List.of(personalCurriculumItemResponse, otherPersonalCurriculumItemResponse));

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/curriculums", 1)
                        .header("Authorization", "1"))
                .andExpect(status().isOk())
                .andDo(document("curriculum-get-personal",
                        pathParameters(
                                parameterWithName("studyId").description("스터디 id")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("member id")
                        ),
                        responseFields(
                                numberFieldWithPath("[].id", "participantCurriculum id"),
                                numberFieldWithPath("[].participantId", "참여자 id"),
                                numberFieldWithPath("[].itemOrder", "스터디 설명"),
                                booleanFieldWithPath("[].isChecked", "체크 여부"),
                                stringFieldWithPath("[].name", "커리큘럼 내용")
                        )
                ));
    }
}
