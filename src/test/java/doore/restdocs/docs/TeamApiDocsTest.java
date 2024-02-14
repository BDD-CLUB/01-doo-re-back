package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import doore.restdocs.RestDocsTest;
import doore.team.api.TeamController;
import doore.team.application.dto.request.TeamCreateRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.RequestPartFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(TeamController.class)
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
}
