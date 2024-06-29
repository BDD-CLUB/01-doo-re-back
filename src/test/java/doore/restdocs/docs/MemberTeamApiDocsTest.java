package doore.restdocs.docs;

import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.member.application.dto.response.TeamMemberResponse;
import doore.restdocs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;

public class MemberTeamApiDocsTest extends RestDocsTest {
    private String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = "mocked-access-token";
        when(jwtTokenGenerator.generateToken(any(String.class))).thenReturn(accessToken);
    }

    @Test
    @DisplayName("팀원 목록을 조회한다.")
    public void 팀원_목록을_조회한다() throws Exception {
        //given
        final QueryParametersSnippet queryParameters = queryParameters(
                parameterWithName("keyword").optional().description("검색 단어(option)")
        );
        final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
                numberFieldWithPath("[].id", "회원 id"),
                stringFieldWithPath("[].name", "회원 이름"),
                stringFieldWithPath("[].email", "회원 이메일"),
                stringFieldWithPath("[].imageUrl", "회원 프로필 이미지 url"),
                stringFieldWithPath("[].teamRole", "회원의 직책"),
                booleanFieldWithPath("[].isDeleted", "회원의 삭제(탈퇴) 여부")
        );
        final List<TeamMemberResponse> response = List.of(
                new TeamMemberResponse(2L, "보름", "borum@naver.com", "https://borum.png", ROLE_팀원, false),
                new TeamMemberResponse(1L, "아마란스", "songsy404@naver.com", "https://amaran-th.png", ROLE_팀장, false),
                new TeamMemberResponse(4L, "아마스빈", "amasbin@naver.com", "https://borum.png", ROLE_팀원, false),
                new TeamMemberResponse(5L, "아마아마아마", "amaamaama@naver.com", "https://zzanggu.png", ROLE_팀원, false),
                new TeamMemberResponse(3L, "짱구", "zzanggu@naver.com", "https://zzanggu.png", ROLE_팀원, false)
        );

        // when
        when(memberTeamQueryService.findMemberTeams(any(), eq(null), any())).thenReturn(response);

        // then
        mockMvc.perform(get("/teams/1/members")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(document("member-team-find", queryParameters, responseFields));
    }

    @Test
    @DisplayName("팀원 목록을 검색해서 조회한다.")
    public void 팀원_목록을_검색해서_조회한다() throws Exception {
        //given
        final QueryParametersSnippet queryParameters = queryParameters(
                parameterWithName("keyword").optional().description("검색 단어(option)")
        );
        final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
                numberFieldWithPath("[].id", "회원 id"),
                stringFieldWithPath("[].name", "회원 이름"),
                stringFieldWithPath("[].email", "회원 이메일"),
                stringFieldWithPath("[].imageUrl", "회원 프로필 이미지 url"),
                stringFieldWithPath("[].teamRole", "회원의 직책"),
                booleanFieldWithPath("[].isDeleted", "회원의 삭제(탈퇴) 여부")
        );
        final List<TeamMemberResponse> response = List.of(
                new TeamMemberResponse(1L, "아마란스", "songsy404@naver.com", "https://amaran-th.png", ROLE_팀장, false),
                new TeamMemberResponse(4L, "아마스빈", "amasbin@naver.com", "https://borum.png", ROLE_팀원, false),
                new TeamMemberResponse(5L, "아마아마아마", "amaamaama@naver.com", "https://zzanggu.png", ROLE_팀원, false)
        );

        // when
        when(memberTeamQueryService.findMemberTeams(any(), any(), any())).thenReturn(response);

        // then
        mockMvc.perform(get("/teams/1/members")
                        .param("keyword", "아마")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(document("member-team-find-search", queryParameters, responseFields));
    }

    @Test
    @DisplayName("팀원을 삭제할 수 있다.")
    public void 팀원을_삭제할_수_있다() throws Exception {
        //when
        doNothing().when(memberTeamCommandService).deleteMemberTeam(any(), any(), any());

        //then
        mockMvc.perform(delete("/teams/{teamId}/members/{memberId}", 1, 1)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNoContent())
                .andDo(document("member-team-delete", pathParameters(
                        parameterWithName("teamId").description("팀 id"),
                        parameterWithName("memberId").description("삭제할 멤버 id"))));
    }
}
