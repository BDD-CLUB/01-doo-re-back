package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.member.domain.Member;
import doore.member.domain.Participant;
import doore.restdocs.RestDocsTest;
import doore.study.api.ParticipantController;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

@WebMvcTest(ParticipantController.class)
public class ParticipantApiDocsTest extends RestDocsTest {

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
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/studies/{studyId}/members", 1)
                        .header("Authorization", "1"))
                .andExpect(status().isNoContent())
                .andDo(document("participant-withdraw",
                        pathParameters(parameterWithName("studyId").description("스터디 id")),
                        requestHeaders(headerWithName("Authorization").description("member id"))
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

        when(participantQueryService.findAllParticipants(any())).thenReturn(List.of(participant));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/studies/{studyId}/members", 1))
                .andExpect(status().isOk())
                .andDo(document("participant-get", pathParameters(
                        parameterWithName("studyId").description("스터디 id"))
                ));
    }
}
