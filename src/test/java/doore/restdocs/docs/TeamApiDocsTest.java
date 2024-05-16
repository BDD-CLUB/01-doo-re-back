package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.crop.response.CropReferenceResponse;
import doore.restdocs.RestDocsTest;
import doore.study.application.dto.response.totalStudyResponse.CurriculumItemReferenceResponse;
import doore.study.application.dto.response.totalStudyResponse.StudySimpleResponse;
import doore.study.domain.StudyStatus;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamInviteCodeRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.application.dto.response.TeamInviteCodeResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.RequestPartFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.web.multipart.MultipartFile;

public class TeamApiDocsTest extends RestDocsTest {

    @Test
    @DisplayName("팀을 생성한다.")
    public void 팀을_생성한다() throws Exception {
        //given
        final TeamCreateRequest request = new TeamCreateRequest("BDD", "개발 동아리 입니다.");
        final MockPart mockPart = getMockPart("request", request);
        mockPart.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        final MockMultipartFile file = getMockImageFile();

        // when
        doNothing().when(teamCommandService).createTeam(any(TeamCreateRequest.class), any(MultipartFile.class));

        // then
        final RequestPartFieldsSnippet requestPartFields = requestPartFields(
                "request",
                stringFieldWithPath("name", "팀 이름"),
                stringFieldWithPath("description", "팀 소개")
        );
        final RequestPartsSnippet requestParts = requestParts(
                partWithName("request").description("팀 정보"),
                partWithName("file").description("팀 이미지 파일")
        );
        mockMvc.perform(multipart("/teams")
                        .part(mockPart)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andDo(document("team-create", requestPartFields, requestParts));
    }

    @Test
    @DisplayName("팀의 정보를 수정한다.")
    public void 팀의_정보를_수정한다() throws Exception {
        //given
        final TeamUpdateRequest request = new TeamUpdateRequest("BDD", "개발 동아리 입니다.");

        // when
        Long teamId = 1L;
        doNothing().when(teamCommandService).updateTeam(eq(teamId), any(TeamUpdateRequest.class));

        // then
        final RequestFieldsSnippet requestFields = requestFields(
                stringFieldWithPath("name", "팀 이름"),
                stringFieldWithPath("description", "팀 소개")
        );
        final PathParametersSnippet pathParameters = pathParameters(
                parameterWithName("teamId").description("팀 ID")
        );
        mockMvc.perform(put("/teams/{teamId}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andDo(document("team-update", requestFields, pathParameters));
    }

    @Test
    @DisplayName("팀의 이미지를 수정한다.")
    public void 팀의_이미지를_수정한다() throws Exception {
        //given
        final MockMultipartFile file = getMockImageFile();

        // when
        Long teamId = 1L;
        doNothing().when(teamCommandService).updateTeamImage(eq(teamId), any(MultipartFile.class));

        // then
        final RequestPartsSnippet requestParts = requestParts(
                partWithName("file").description("팀 이미지 파일")
        );
        final PathParametersSnippet pathParameters = pathParameters(
                parameterWithName("teamId").description("팀 ID")
        );
        mockMvc.perform(multipart("/teams/{teamId}/image", teamId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNoContent())
                .andDo(document("team-image-update", requestParts, pathParameters));
    }

    @Test
    @DisplayName("팀을 삭제한다.")
    public void 팀을_삭제한다() throws Exception {
        // when
        Long teamId = 1L;
        doNothing().when(teamCommandService).deleteTeam(eq(teamId));

        // then
        final PathParametersSnippet pathParameters = pathParameters(
                parameterWithName("teamId").description("팀 ID")
        );
        mockMvc.perform(delete("/teams/{teamId}", teamId)).andExpect(status().isNoContent())
                .andDo(document("team-delete", pathParameters));
    }

    @Test
    @DisplayName("팀의 초대코드를 생성한다.")
    public void 팀의_초대코드를_생성한다() throws Exception {
        // given
        Long teamId = 1L;
        final TeamInviteCodeResponse response = new TeamInviteCodeResponse("asdf");

        // when
        when(teamCommandService.generateTeamInviteCode(eq(teamId))).thenReturn(response);

        // then
        final PathParametersSnippet pathParameters = pathParameters(
                parameterWithName("teamId").description("팀 ID")
        );
        final ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                stringFieldWithPath("code", "생성된 팀의 초대 코드")
        );
        mockMvc.perform(post("/teams/{teamId}/invite-code", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("team-create-invite-code", pathParameters, responseFieldsSnippet));
    }

    @Test
    @DisplayName("초대코드를 통해 팀에 가입한다.")
    public void 초대코드를_통해_팀에_가입한다() throws Exception {
        // given
        Long teamId = 1L;
        final TeamInviteCodeRequest request = new TeamInviteCodeRequest("asdf");

        // when
        doNothing().when(teamCommandService).joinTeam(eq(teamId), any(TeamInviteCodeRequest.class));

        // then
        final PathParametersSnippet pathParameters = pathParameters(
                parameterWithName("teamId").description("팀 ID")
        );
        final RequestFieldsSnippet requestFieldsSnippet = requestFields(
                stringFieldWithPath("code", "팀의 초대 코드")
        );
        mockMvc.perform(post("/teams/{teamId}/join", teamId)
                        .content(asJsonString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("team-join", pathParameters, requestFieldsSnippet));
    }

    @Test
    @DisplayName("나의 팀과 스터디 목록을 조회한다")
    void 나의_팀과_스터디_목록을_조회한다() throws Exception {
        //given
        final String FAKE_BEARER_ACCESS_TOKEN = "Bearer AccessToken";
        final Long memberId = 1L;
        final List<TeamReferenceResponse> teamResponse = List.of(
                new TeamReferenceResponse(1L, "BDD", "개발 동아리입니다", "image.png"),
                new TeamReferenceResponse(3L, "KEEPER", "보안 동아리입니다", "image.png")
        );

        final StudySimpleResponse studyResponse = getStudySimpleResponse();

        final PathParametersSnippet pathParameters = pathParameters(
                parameterWithName("memberId").description("팀과 스터디 목록을 조회하고자 하는 회원 ID")
        );
        final ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                numberFieldWithPath("teamReferenceResponses.[].id", "팀의 ID"),
                stringFieldWithPath("teamReferenceResponses.[].name", "팀의 이름"),
                stringFieldWithPath("teamReferenceResponses.[].description", "팀의 설명"),
                stringFieldWithPath("teamReferenceResponses.[].imageUrl", "팀의 프로필 이미지 url"),
                numberFieldWithPath("studySimpleResponses.[].id", "스터디의 ID"),
                stringFieldWithPath("studySimpleResponses.[].name", "스터디의 이름"),
                stringFieldWithPath("studySimpleResponses.[].description", "스터디의 설명"),
                stringFieldWithPath("studySimpleResponses.[].startDate", "스터디의 시작일"),
                stringFieldWithPath("studySimpleResponses.[].endDate", "스터디의 종료일"),
                stringFieldWithPath("studySimpleResponses.[].status", "스터디의 진행 상태"),
                booleanFieldWithPath("studySimpleResponses.[].isDeleted", "스터디의 삭제 여부"),
                numberFieldWithPath("studySimpleResponses.[].teamReference.id", "스터디가 속한 팀의 ID"),
                stringFieldWithPath("studySimpleResponses.[].teamReference.name", "스터디가 속한 팀의 이름"),
                stringFieldWithPath("studySimpleResponses.[].teamReference.description", "스터디가 속한 팀의 설명"),
                stringFieldWithPath("studySimpleResponses.[].teamReference.imageUrl", "스터디가 속한 팀의 이미지 url"),
                numberFieldWithPath("studySimpleResponses.[].cropReference.id", "스터디의 작물의 ID"),
                stringFieldWithPath("studySimpleResponses.[].cropReference.name", "스터디의 작물의 이름"),
                stringFieldWithPath("studySimpleResponses.[].cropReference.imageUrl", "스터디의 작물의 이미지 url"),
                numberFieldWithPath("studySimpleResponses.[].curriculumItems[].id", "스터디의 커리큘럼 ID"),
                stringFieldWithPath("studySimpleResponses.[].curriculumItems[].name", "스터디의 커리큘럼 이름"),
                numberFieldWithPath("studySimpleResponses.[].curriculumItems[].itemOrder", "스터디의 커리큘럼 순서번호"),
                booleanFieldWithPath("studySimpleResponses.[].curriculumItems[].isDeleted", "스터디의 커리큘럼 삭제여부")
        );

        //when
        when(teamQueryService.findMyTeams(memberId)).thenReturn(teamResponse);
        when(studyQueryService.findMyStudies(memberId)).thenReturn(List.of(studyResponse));

        //then
        mockMvc.perform(get("/teams/members/{memberId}", memberId)
                        .header(HttpHeaders.AUTHORIZATION, FAKE_BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("my-teams-and-studies", pathParameters, responseFieldsSnippet));

    }

    private static StudySimpleResponse getStudySimpleResponse() {
        final TeamReferenceResponse teamReferenceResponse =
                new TeamReferenceResponse(1L, "개발 동아리 BDD", "개발 동아리 BDD입니다!", "https://~");
        final CropReferenceResponse cropReferenceResponse = new CropReferenceResponse(1L, "벼", "https://~");
        final CurriculumItemReferenceResponse curriculumItemReferenceResponse = new CurriculumItemReferenceResponse(
                1L, "chapter1. greedy", 1, false);
        final StudySimpleResponse response = new StudySimpleResponse(1L, "알고리즘", "알고리즘 스터디입니다.",
                LocalDate.parse("2020-01-01"),
                LocalDate.parse("2020-02-01"), StudyStatus.IN_PROGRESS, false, teamReferenceResponse,
                cropReferenceResponse, List.of(curriculumItemReferenceResponse));
        return response;
    }
}

