package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.crop.response.CropReferenceResponse;
import doore.restdocs.RestDocsTest;
import doore.study.api.StudyController;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.personalStudyResponse.PersonalCurriculumItemResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.application.dto.response.totalStudyResponse.CurriculumItemReferenceResponse;
import doore.study.application.dto.response.totalStudyResponse.CurriculumItemResponse;
import doore.study.application.dto.response.totalStudyResponse.ParticipantCurriculumItemResponse;
import doore.study.application.dto.response.totalStudyResponse.StudyDetailResponse;
import doore.study.application.dto.response.totalStudyResponse.StudySimpleResponse;
import doore.study.domain.StudyStatus;
import doore.team.application.dto.response.TeamReferenceResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(StudyController.class)
public class StudyApiDocsTest extends RestDocsTest {

    @Test
    @DisplayName("스터디를 생성한다.")
    public void 스터디를_생성한다() throws Exception {
        StudyCreateRequest request = StudyCreateRequest.builder()
                .name("알고리즘")
                .description("알고리즘 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .cropId(1L)
                .curriculumItems(new ArrayList<CurriculumItemRequest>())
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/teams/{teamId}/studies", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("study-create", pathParameters(
                                parameterWithName("teamId")
                                        .description("스터디 id")),
                        requestFields(
                                stringFieldWithPath("name", "스터디 이름"),
                                stringFieldWithPath("description", "스터디 설명"),
                                stringFieldWithPath("startDate", "시작 날짜"),
                                stringFieldWithPath("endDate", "종료 날짜"),
                                numberFieldWithPath("cropId", "작물 id"),
                                arrayFieldWithPath("curriculumItems", "커리큘럼 아이템 리스트")
                        )
                ));
    }

    @Test
    @DisplayName("스터디 전체 정보를 조회한다.")
    public void 스터디_전체_정보를_조회한다() throws Exception {
        StudyDetailResponse studyDetailResponse = getStudyDetailResponse();

        when(studyQueryService.findStudyById(any())).thenReturn(studyDetailResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/all", 1))
                .andExpect(status().isOk())
                .andDo(document("study-get-all", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
                ));
    }

    private StudyDetailResponse getStudyDetailResponse() {
        TeamReferenceResponse teamReferenceResponse =
                new TeamReferenceResponse(1L, "개발 동아리 BDD", "개발 동아리 BDD입니다!", "https://~");
        CropReferenceResponse cropReferenceResponse = new CropReferenceResponse(1L, "벼", "https://~");

        ParticipantCurriculumItemResponse participantCurriculumItemResponse =
                new ParticipantCurriculumItemResponse(1L, false);
        CurriculumItemResponse curriculumItemResponse = new CurriculumItemResponse(
                1L, "chapter1. greedy", 0, false, List.of(participantCurriculumItemResponse));

        return new StudyDetailResponse(1L, "알고리즘", "알고리즘 스터디입니다.", LocalDate.parse("2020-01-01"),
                LocalDate.parse("2020-02-01"), StudyStatus.IN_PROGRESS, false, teamReferenceResponse,
                cropReferenceResponse, List.of(curriculumItemResponse));
    }

    @Test
    @DisplayName("스터디를 조회한다.")
    public void 스터디를_조회한다() throws Exception {
        TeamReferenceResponse teamReferenceResponse =
                new TeamReferenceResponse(1L, "개발 동아리 BDD", "개발 동아리 BDD입니다!", "https://~");
        CropReferenceResponse cropReferenceResponse = new CropReferenceResponse(1L, "벼", "https://~");

        PersonalCurriculumItemResponse personalCurriculumItemResponse = new PersonalCurriculumItemResponse(
                1L, "chapter1. greedy", 1, false, false);
        PersonalStudyDetailResponse personalStudyDetailResponse = new PersonalStudyDetailResponse(
                1L, "알고리즘", "알고리즘 스터디입니다.", LocalDate.parse("2020-01-01"),
                LocalDate.parse("2020-02-01"), StudyStatus.IN_PROGRESS, false, teamReferenceResponse,
                cropReferenceResponse, 1L, List.of(personalCurriculumItemResponse));

        when(studyQueryService.getPersonalStudyDetail(any(), any())).thenReturn(personalStudyDetailResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}", 1)
                        .header("Authorization", "1"))
                .andExpect(status().isOk())
                .andDo(document("study-get-personal",
                        pathParameters(
                                parameterWithName("studyId").description("스터디 id")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("member id")
                        )
                ));
    }

    @Test
    @DisplayName("스터디를 삭제한다.")
    public void 스터디를_삭제한다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}", 1))
                .andExpect(status().isNoContent())
                .andDo(document("study-delete", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
                ));
    }

    @Test
    @DisplayName("스터디를 수정한다.")
    public void 스터디를_수정한다() throws Exception {
        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .name("스프링")
                .description("스프링 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .status(StudyStatus.IN_PROGRESS)
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders.put("/studies/{studyId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("study-update",
                        pathParameters(
                                parameterWithName("studyId")
                                        .description("스터디 id")
                        ),
                        requestFields(
                                stringFieldWithPath("name", "스터디 이름"),
                                stringFieldWithPath("description", "스터디 설명"),
                                stringFieldWithPath("startDate", "시작 날짜"),
                                stringFieldWithPath("endDate", "종료 날짜"),
                                stringFieldWithPath("status", "현재 상태")
                        )
                ));
    }

    @Test
    @DisplayName("스터디의 상태를 수정한다.")
    public void 스터디의_상태를_수정한다() throws Exception {
        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/studies/{studyId}/status?status={status}", 1, "IN_PROGRESS"))
                .andExpect(status().isNoContent())
                .andDo(document("study-change-status",
                        pathParameters(
                                parameterWithName("studyId").description("스터디 id")
                        ),
                        queryParameters(
                                parameterWithName("status").description("현재 스터디 상태 (UPCOMING, IN_PROGRESS, ENDED)")
                        )
                ));
    }

    @Test
    @DisplayName("스터디를 종료한다.")
    public void 스터디를_종료한다() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/studies/{studyId}/termination", 1))
                .andExpect(status().isNoContent())
                .andDo(document("study-terminate", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
                ));
    }

    @Test
    @DisplayName("나의 스터디 목록을 조회한다.")
    public void 나의_스터디_목록을_조회한다() throws Exception {
        final Long memberId = 1L;
        final String FAKE_BEARER_ACCESS_TOKEN = "Bearer AccessToken";
        final TeamReferenceResponse teamReferenceResponse =
                new TeamReferenceResponse(1L, "개발 동아리 BDD", "개발 동아리 BDD입니다!", "https://~");
        final CropReferenceResponse cropReferenceResponse = new CropReferenceResponse(1L, "벼", "https://~");
        final CurriculumItemReferenceResponse curriculumItemReferenceResponse = new CurriculumItemReferenceResponse(
                1L, "chapter1. greedy", 1, false);
        final StudySimpleResponse response = new StudySimpleResponse(1L, "알고리즘", "알고리즘 스터디입니다.",
                LocalDate.parse("2020-01-01"),
                LocalDate.parse("2020-02-01"), StudyStatus.IN_PROGRESS, false, teamReferenceResponse,
                cropReferenceResponse, List.of(curriculumItemReferenceResponse));

        final ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                numberFieldWithPath("[].id", "스터디의 ID"),
                stringFieldWithPath("[].name", "스터디의 이름"),
                stringFieldWithPath("[].description", "스터디의 설명"),
                stringFieldWithPath("[].startDate", "스터디의 시작일"),
                stringFieldWithPath("[].endDate", "스터디의 종료일"),
                stringFieldWithPath("[].status", "스터디의 진행 상태"),
                booleanFieldWithPath("[].isDeleted", "스터디의 삭제 여부"),
                numberFieldWithPath("[].teamReference.id", "스터디가 속한 팀의 ID"),
                stringFieldWithPath("[].teamReference.name", "스터디가 속한 팀의 이름"),
                stringFieldWithPath("[].teamReference.description", "스터디가 속한 팀의 설명"),
                stringFieldWithPath("[].teamReference.imageUrl", "스터디가 속한 팀의 이미지 url"),
                numberFieldWithPath("[].cropReference.id", "스터디의 작물의 ID"),
                stringFieldWithPath("[].cropReference.name", "스터디의 작물의 이름"),
                stringFieldWithPath("[].cropReference.imageUrl", "스터디의 작물의 이미지 url"),
                numberFieldWithPath("[].curriculumItems[].id", "스터디의 커리큘럼 ID"),
                stringFieldWithPath("[].curriculumItems[].name", "스터디의 커리큘럼 이름"),
                numberFieldWithPath("[].curriculumItems[].itemOrder", "스터디의 커리큘럼 순서번호"),
                booleanFieldWithPath("[].curriculumItems[].isDeleted", "스터디의 커리큘럼 삭제여부")
        );

        when(studyQueryService.findMyStudies(memberId)).thenReturn(List.of(response));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/members/{memberId}", memberId)
                        .header(HttpHeaders.AUTHORIZATION, FAKE_BEARER_ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(document("my-studies",
                        pathParameters(
                                parameterWithName("memberId").description("회원 id")
                        ),
                        responseFieldsSnippet
                ));
    }
}
