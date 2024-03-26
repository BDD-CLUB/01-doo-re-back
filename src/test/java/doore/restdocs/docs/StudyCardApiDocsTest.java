package doore.restdocs.docs;

import static doore.document.domain.DocumentAccessType.ALL;
import static doore.document.domain.DocumentType.IMAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.crop.domain.dto.response.CropReferenceResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.application.dto.response.FileResponse;
import doore.restdocs.RestDocsTest;
import doore.study.api.StudyCardController;
import doore.study.application.dto.response.StudyCardResponse;
import doore.study.application.dto.response.StudyResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalCurriculumItemResponse;
import doore.study.application.dto.response.personalStudyResponse.PersonalStudyDetailResponse;
import doore.study.domain.StudyStatus;
import doore.team.application.dto.response.TeamReferenceResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(StudyCardController.class)
public class StudyCardApiDocsTest extends RestDocsTest {
    PersonalCurriculumItemResponse personalCurriculumItemResponse = new PersonalCurriculumItemResponse(
            1L, "chapter1. greedy", 1, false, false);
    PersonalStudyDetailResponse personalStudyDetailResponse = new PersonalStudyDetailResponse(
            getStudyResponse(), 1L, List.of(personalCurriculumItemResponse));
    FileResponse fileResponse = new FileResponse(1L, "s3 url");
    DocumentDetailResponse documentDetailResponse = DocumentDetailResponse.builder()
            .id(1L)
            .title("학습자료")
            .description("학습자료 입니다.")
            .accessType(ALL)
            .type(IMAGE)
            .files(List.of(fileResponse))
            .date(LocalDate.parse("2024-02-28"))
            .uploader("김땡땡")
            .build();

    @Test
    @DisplayName("스터디 카드 목록을 조회한다.")
    public void 스터디_카드_목록을_조회한다() throws Exception {
        //given

        //when
        when(studyCardQueryService.getStudyCards(any())).thenReturn(List.of(personalStudyDetailResponse));

        //then
        mockMvc.perform(get("/studyCards").header("Authorization", "1"))
                .andExpect(status().isOk())
                .andDo(document("studyCard-get-list",
                        requestHeaders(headerWithName("Authorization").description("member id"))
                ));
    }

    @Test
    @DisplayName("스터디 카드를 조회한다.")
    public void 스터디_카드를_조회한다() throws Exception {
        //given & when
        when(studyCardQueryService.getStudyCard(any(), any())).thenReturn(personalStudyDetailResponse);
        when(documentQueryService.getUploadedDocumentsByMember(any())).thenReturn(List.of(documentDetailResponse));

        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/studyCards/{studyId}", 1).header("Authorization", "1"))
                .andExpect(status().isOk())
                .andDo(document("studyCard-get",
                        pathParameters(parameterWithName("studyId").description("스터디 id")),
                        requestHeaders(headerWithName("Authorization").description("member id"))
                ));
    }

    @Test
    @DisplayName("선택한 스터디 카드 목록을 조회한다.")
    public void 선택한_스터디_카드_목록을_조회한다() throws Exception {
        //given

        //when
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("studyIds", "1");
        params.add("studyIds", "2");

        when(studyCardQueryService.getStudyCard(any(), any())).thenReturn(personalStudyDetailResponse);
        when(documentQueryService.getUploadedDocumentsByMember(any())).thenReturn(List.of(documentDetailResponse));

        //then
        mockMvc.perform(get("/studyCards/selections", 1).header("Authorization", "1").params(params))
                .andExpect(status().isOk())
                .andDo(document("studyCard-get-selectedList",
                        requestHeaders(headerWithName("Authorization").description("member id")),
                        queryParameters(parameterWithName("studyIds").description("선택한 스터디 카드의 스터디 아이디"))
                ));
    }

    private StudyResponse getStudyResponse() {
        TeamReferenceResponse teamReferenceResponse =
                new TeamReferenceResponse(1L, "개발 동아리 BDD", "개발 동아리 BDD입니다!", "https://~");
        CropReferenceResponse cropReferenceResponse = new CropReferenceResponse(1L, "벼", "https://~");

        return new StudyResponse(1L, "알고리즘", "알고리즘 스터디입니다.", LocalDate.parse("2020-01-01"),
                LocalDate.parse("2020-02-01"), StudyStatus.IN_PROGRESS, false, teamReferenceResponse,
                cropReferenceResponse);
    }
}
