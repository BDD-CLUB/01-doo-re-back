package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.member.api.MemberTeamController;
import doore.member.application.dto.response.MemberResponse;
import doore.restdocs.RestDocsTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;

@WebMvcTest(MemberTeamController.class)
public class MemberTeamApiDocsTest extends RestDocsTest {

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
                stringFieldWithPath("[].role", "회원의 직책(추후 수정 예정)"),
                booleanFieldWithPath("[].isDeleted", "회원의 삭제(탈퇴) 여부")
        );
        final List<MemberResponse> response = List.of(
                new MemberResponse(2L, "보름", "borum@naver.com", "https://borum.png", "팀원", false),
                new MemberResponse(1L, "아마란스", "songsy404@naver.com", "https://amaran-th.png", "팀장", false),
                new MemberResponse(4L, "아마스빈", "amasbin@naver.com", "https://borum.png", "팀원", false),
                new MemberResponse(5L, "아마아마아마", "amaamaama@naver.com", "https://zzanggu.png", "팀원", false),
                new MemberResponse(3L, "짱구", "zzanggu@naver.com", "https://zzanggu.png", "팀원", false)
        );

        // when
        when(memberTeamQueryService.findMemberTeams(anyLong(), eq(null))).thenReturn(response);

        // then
        mockMvc.perform(get("/teams/1/members")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
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
                stringFieldWithPath("[].role", "회원의 직책(추후 수정 예정)"),
                booleanFieldWithPath("[].isDeleted", "회원의 삭제(탈퇴) 여부")
        );
        final List<MemberResponse> response = List.of(
                new MemberResponse(1L, "아마란스", "songsy404@naver.com", "https://amaran-th.png", "팀장", false),
                new MemberResponse(4L, "아마스빈", "amasbin@naver.com", "https://borum.png", "팀원", false),
                new MemberResponse(5L, "아마아마아마", "amaamaama@naver.com", "https://zzanggu.png", "팀원", false)
        );

        // when
        when(memberTeamQueryService.findMemberTeams(anyLong(), anyString())).thenReturn(response);

        // then
        mockMvc.perform(get("/teams/1/members")
                        .param("keyword", "아마")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(document("member-team-find-search", queryParameters, responseFields));
    }
}
