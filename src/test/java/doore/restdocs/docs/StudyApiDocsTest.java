package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import doore.crop.response.CropReferenceResponse;
import doore.restdocs.RestDocsTest;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.StudyRankResponse;
import doore.study.application.dto.response.StudyReferenceResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.domain.StudyStatus;
import doore.team.application.dto.response.TeamReferenceResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class StudyApiDocsTest extends RestDocsTest {
    private String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = "mocked-access-token";
        when(jwtTokenGenerator.generateToken(any(String.class))).thenReturn(accessToken);
    }

    @Test
    @DisplayName("스터디를 생성한다.")
    public void 스터디를_생성한다() throws Exception {
        final StudyCreateRequest request = StudyCreateRequest.builder()
                .name("알고리즘")
                .description("알고리즘 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .cropId(1L)
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders.post("/teams/{teamId}/studies", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(org.springframework.http.HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isCreated())
                .andDo(document("study-create", pathParameters(
                                parameterWithName("teamId")
                                        .description("스터디 id")),
                        requestFields(
                                stringFieldWithPath("name", "스터디 이름"),
                                stringFieldWithPath("description", "스터디 설명"),
                                stringFieldWithPath("startDate", "시작 날짜"),
                                stringFieldWithPath("endDate", "종료 날짜"),
                                numberFieldWithPath("cropId", "작물 id")
                        )
                ));
    }

    @Test
    @DisplayName("스터디 정보를 조회한다.")
    public void 스터디_정보를_조회한다() throws Exception {
        final StudyResponse studyResponse = getStudyResponse();

        when(studyQueryService.findStudyById(any())).thenReturn(studyResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}", 1))
                .andExpect(status().isOk())
                .andDo(document("study-get", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
                ));
    }

    private StudyResponse getStudyResponse() {
        final TeamReferenceResponse teamReferenceResponse =
                new TeamReferenceResponse(1L, "개발 동아리 BDD", "개발 동아리 BDD입니다!", "https://~");
        final CropReferenceResponse cropReferenceResponse = new CropReferenceResponse(1L, "벼", "https://~");

        return StudyResponse.builder()
                .id(1L)
                .name("알고리즘")
                .description("알고리즘 스터디입니다.")
                .startDate(LocalDate.parse("2020-01-01"))
                .endDate(LocalDate.parse("2020-01-02"))
                .status(StudyStatus.IN_PROGRESS)
                .teamReference(teamReferenceResponse)
                .cropReference(cropReferenceResponse)
                .studyProgressRatio(50)
                .studyLeaderId(1L)
                .build();
    }

    private StudyReferenceResponse getStudyReferenceResponse() {
        return StudyReferenceResponse.builder()
                .id(1L)
                .name("알고리즘")
                .description("알고리즘 스터디입니다.")
                .startDate(LocalDate.parse("2020-01-01"))
                .endDate(LocalDate.parse("2020-01-02"))
                .status(StudyStatus.IN_PROGRESS)
                .cropId(1L)
                .studyProgressRatio(50)
                .build();
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
        final StudyUpdateRequest request = StudyUpdateRequest.builder()
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
        final List<StudyReferenceResponse> response = List.of(
                getStudyReferenceResponse(),
                getStudyReferenceResponse()
        );

        final ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                numberFieldWithPath("[].id", "스터디의 ID"),
                stringFieldWithPath("[].name", "스터디의 이름"),
                stringFieldWithPath("[].description", "스터디의 설명"),
                stringFieldWithPath("[].startDate", "스터디의 시작일"),
                stringFieldWithPath("[].endDate", "스터디의 종료일"),
                stringFieldWithPath("[].status", "스터디의 진행 상태"),
                numberFieldWithPath("[].cropId", "스터디의 작물 ID"),
                numberFieldWithPath("[].studyProgressRatio", "스터디 진행률")
        );

        when(studyQueryService.findMyStudies(any(), any())).thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/members/{memberId}", memberId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(document("my-studies",
                        pathParameters(
                                parameterWithName("memberId").description("회원 id")
                        ),
                        responseFieldsSnippet));
    }

    @Test
    @DisplayName("팀의 스터디 목록(스터디 랭킹)을 조회한다.")
    public void 팀의_스터디_목록스터디_랭킹을_조회한다() throws Exception {
        final StudyReferenceResponse studyReferenceResponse =
                new StudyReferenceResponse(1L, "study1", "this is study 1", LocalDate.of(2024, 7, 6),
                        LocalDate.of(2024, 7, 7), StudyStatus.IN_PROGRESS, 1L, 60L);
        final StudyRankResponse studyRankResponse = new StudyRankResponse(0, studyReferenceResponse);
        final StudyReferenceResponse otherStudyReferenceResponse =
                new StudyReferenceResponse(2L, "study2", "this is study 2", LocalDate.of(2024, 7, 6),
                        LocalDate.of(2024, 8, 7), StudyStatus.IN_PROGRESS, 2L, 50L);
        final StudyRankResponse otherStudyRankResponse = new StudyRankResponse(20, otherStudyReferenceResponse);

        when(studyQueryService.getTeamStudies(any(), any())).thenReturn(
                List.of(studyRankResponse, otherStudyRankResponse));

        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            add("page", "0");
            add("size", "4");
        }};
        mockMvc.perform(get("/teams/{teamId}/studies", 1).params(params))
                .andExpect(status().isOk())
                .andDo(document("team-studies-get",
                        pathParameters(
                                parameterWithName("teamId").description("팀 id")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 (default: 0)"),
                                parameterWithName("size").description("한 페이지당 불러올 개수 (default: 4)")
                        )));
    }
}
