package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.member.application.dto.response.MemberAndMyTeamsAndStudiesResponse;
import doore.restdocs.RestDocsTest;
import doore.study.application.dto.response.StudyNameResponse;
import doore.team.application.dto.response.MyTeamsAndStudiesResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;

public class MemberApiDocsTest extends RestDocsTest {
    private String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = "mocked-access-token";
        when(jwtTokenGenerator.generateToken(any(String.class))).thenReturn(accessToken);
    }

    @Test
    @DisplayName("[성공] 유효한 요청이면 팀장 권한이 정상적으로 위임된다.")
    void transferTeamLeader_유효한_요청이면_팀장_권한이_정상적으로_위임된다() throws Exception {
        doNothing().when(memberCommandService).transferTeamLeader(any(), any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/teams/{teamId}/mandate/{newTeamLeaderId}", 1, 1)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isNoContent())
                .andDo(document("transfer-team-Leader", pathParameters(
                        parameterWithName("teamId").description("팀 id"),
                        parameterWithName("newTeamLeaderId").description("변경될 팀장 id"))));
    }

    @Test
    @DisplayName("[성공] 유효한 요청이면 스터디장 권한이 정상적으로 위임된다.")
    void transferStudyLeader_유효한_요청이면_스터디장_권한이_정상적으로_위임된다() throws Exception {
        doNothing().when(memberCommandService).transferStudyLeader(any(), any(), any());

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/study/{studyId}/mandate/{newStudyLeaderId}", 1, 1)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isNoContent())
                .andDo(document("transfer-study-Leader", pathParameters(
                        parameterWithName("studyId").description("스터디 id"),
                        parameterWithName("newStudyLeaderId").description("변경될 스터디장 id"))));
    }

    @Test
    @DisplayName("[성공] 유효한 요청이면 회원 탈퇴에 성공한다.")
    void deleteMember_유효한_요청이면_회원_탈퇴에_성공한다() throws Exception {
        doNothing().when(memberCommandService).deleteMember(any());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/members")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isNoContent())
                .andDo(document("delete-member"));
    }

    @Test
    @DisplayName("[성공] 사이드바에 들어가는 정보를 조회한다.")
    void getSideBarInfo_사이드바에_들어가는_정보를_조회한다_성공() throws Exception {
        //given
        final Long memberId = 1L;
        final List<StudyNameResponse> studyResponses = List.of(
                new StudyNameResponse(1L, "알고리즘 스터디"),
                new StudyNameResponse(2L, "개발 스터디")
        );
        final List<MyTeamsAndStudiesResponse> response = List.of(
                new MyTeamsAndStudiesResponse(1L, "BDD", studyResponses)
        );
        final MemberAndMyTeamsAndStudiesResponse memberAndMyTeamsAndStudiesResponse = new MemberAndMyTeamsAndStudiesResponse(
                1L, "이름", "프로필사진", response);
        final PathParametersSnippet pathParameters = pathParameters(
                parameterWithName("memberId").description("사이드바 정보목록을 조회하는 회원 ID")
        );
        final ResponseFieldsSnippet responseFieldsSnippet = responseFields(
                numberFieldWithPath("id", "멤버 ID"),
                stringFieldWithPath("name", "멤버 이름"),
                stringFieldWithPath("imageUrl", "멤버 프로필 경로"),
                numberFieldWithPath("myTeamsAndStudies[].teamId", "팀 ID"),
                stringFieldWithPath("myTeamsAndStudies[].teamName", "팀 이름"),
                numberFieldWithPath("myTeamsAndStudies[].teamStudies[].id", "팀에 포함되는 스터디 id"),
                stringFieldWithPath("myTeamsAndStudies.[].teamStudies[].name", "팀에 포함되는 스터디 이름")
        );

        //when
        when(memberQueryService.getSideBarInfo(any(), any())).thenReturn(memberAndMyTeamsAndStudiesResponse);

        //then
        mockMvc.perform(get("/members/{memberId}", memberId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-sidebar-info", pathParameters, responseFieldsSnippet));
    }
}
