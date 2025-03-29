package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.member.application.dto.request.MyPageUpdateRequest;
import doore.member.application.dto.response.MemberAndMyTeamsAndStudiesResponse;
import doore.restdocs.RestDocsTest;
import doore.study.application.dto.response.StudyNameResponse;
import doore.study.application.dto.response.StudyReferenceResponse;
import doore.study.domain.StudyStatus;
import doore.team.application.dto.response.MyTeamsAndStudiesResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.web.multipart.MultipartFile;

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

    @Test
    @DisplayName("[성공] 마이페이지를 수정한다.")
    public void updateMyPage_마이페이지를_수정한다_성공() throws Exception {
        final MyPageUpdateRequest request = new MyPageUpdateRequest("수정된 이름");

        mockMvc.perform(RestDocumentationRequestBuilders.patch("/members/me", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(document("myPage-update",
                        requestFields(
                                stringFieldWithPath("name", "수정할 이름")
                        )
                ));
    }

    @Test
    @DisplayName("[성공] 마이페이지의 이미지를 수정한다.")
    public void updateMyPageImage_마이페이지의_이미지를_수정한다_성공() throws Exception {
        final MockMultipartFile file = getMockImageFile();

        doNothing().when(memberCommandService).updateMyPageImage(any(MultipartFile.class), any());

        final RequestPartsSnippet requestParts = requestParts(
                partWithName("file").description("마이페이지 프로필 이미지 파일")
        );

        mockMvc.perform(multipart("/members/me/image", 1L)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isNoContent())
                .andDo(document("myPage-image-update", requestParts));
    }

    @Test
    @DisplayName("[성공] 마이페이지의 이미지를 삭제한다.")
    public void deleteMyPageImage_마이페이지의_이미지를_삭제한다_성공() throws Exception {
        doNothing().when(memberCommandService).deleteMyPageImage(any());

        mockMvc.perform(delete("/members/me/image", 1L)
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isNoContent())
                .andDo(document("myPage-image-delete"));
    }

    @Test
    @DisplayName("[성공] 본인이 가입한 스터디를 모두 조회한다.")
    void getMyPageStudies_본인이_가입한_스터디를_모두_조회한다_성공() throws Exception {
        final Long memberId = 1L;
        final List<StudyReferenceResponse> response = List.of(
                StudyReferenceResponse.builder()
                        .id(1L)
                        .name("자료구조")
                        .description("자료구조 스터디입니다.")
                        .startDate(LocalDate.parse("2025-02-01"))
                        .endDate(LocalDate.parse("2025-02-28"))
                        .status(StudyStatus.ENDED)
                        .cropId(1L)
                        .studyProgressRatio(50)
                        .build(),
                StudyReferenceResponse.builder()
                        .id(2L)
                        .name("알고리즘")
                        .description("알고리즘 스터디입니다.")
                        .startDate(LocalDate.parse("2025-03-01"))
                        .endDate(LocalDate.parse("2025-03-31"))
                        .status(StudyStatus.IN_PROGRESS)
                        .cropId(1L)
                        .studyProgressRatio(50)
                        .build()
        );

        when(studyQueryService.getMyStudies(any(), any())).thenReturn(response);

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

        mockMvc.perform(RestDocumentationRequestBuilders.get("/members/me/studies")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("myPage-studies-get-list", responseFieldsSnippet));
    }
}
