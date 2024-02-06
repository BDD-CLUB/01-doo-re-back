package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.application.dto.response.CurriculumItemResponse;
import doore.study.application.dto.response.StudyDetailResponse;
import doore.study.domain.CurriculumItem;
import doore.study.domain.StudyStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import doore.restdocs.RestDocsTest;
import doore.study.api.StudyController;
import doore.study.application.dto.request.StudyCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

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
    @DisplayName("스터디를 조회한다.")
    public void 스터디를_조회한다() throws Exception {
        CurriculumItem curriculumItem = CurriculumItem.builder()
                .name("챕터 1. 환경설정 완료하기")
                .itemOrder(0)
                .study(null)
                .build();
        StudyDetailResponse studyDetailResponse = new StudyDetailResponse(1L, "알고리즘", "알고리즘 스터디입니다.",
                LocalDate.parse("2020-01-01"), LocalDate.parse("2020-02-01"), StudyStatus.IN_PROGRESS, false, 1L, 1L,
                List.of(CurriculumItemResponse.from(curriculumItem)));

        when(studyQueryService.findStudyById(any())).thenReturn(studyDetailResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}", 1))
                .andExpect(status().isOk())
                .andDo(document("study-get", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
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
    @DisplayName("참여자를 추가한다.")
    void 참여자를_추가한다_성공() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/studies/{studyId}/members/{memberId}", 1, 1))
                .andExpect(status().isCreated())
                .andDo(document("participant-save", pathParameters(
                                parameterWithName("studyId").description("스터디 id"),
                                parameterWithName("memberId").description("회원 id")
                        )
                ));
    }

    @Test
    @DisplayName("참여자를 삭제한다.")
    void 참여자를_삭제한다_성공() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/members/{memberId}", 1, 1))
                .andExpect(status().isNoContent())
                .andDo(document("participant-delete", pathParameters(
                                parameterWithName("studyId").description("스터디 id"),
                                parameterWithName("memberId").description("회원 id")
                        )
                ));
    }

    @Test
    @DisplayName("참여자가 탈퇴한다.")
    void 참여자가_탈퇴한다_성공() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/members", 1))
                .andExpect(status().isNoContent())
                .andDo(document("participant-withdraw", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
                ));
    }

    @Test
    @DisplayName("참여자를 조회한다.")
    void 참여자를_조회한다_성공() throws Exception {
        Member member = Member.builder()
                .id(1L)
                .name("팜")
                .email("pom@gmail.com")
                .googleId("0123456789")
                .imageUrl(null)
                .build();
        Participant participant = Participant.builder()
                .studyId(1L)
                .member(member)
                .build();

        when(studyQueryService.findAllParticipants(any())).thenReturn(List.of(participant));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/members", 1))
                .andExpect(status().isOk())
                .andDo(document("participant-get", pathParameters(
                        parameterWithName("studyId")
                                .description("스터디 id"))
                ));
    }
}
