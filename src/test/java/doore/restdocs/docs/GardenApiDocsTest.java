package doore.restdocs.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;


import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.garden.application.dto.response.DayGardenResponse;
import doore.restdocs.RestDocsTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GardenApiDocsTest extends RestDocsTest {

    @Test
    @DisplayName("[성공] 팀의 올해 텃밭을 조회한다.")
    public void getAllGarden_팀의_올해_텃밭을_생성한다() throws Exception {
        //given
        final List<DayGardenResponse> gardenResponse = List.of(
                DayGardenResponse.builder()
                        .contributeDate(LocalDate.of(2024,1,1))
                        .contributeCount(2)
                        .build(),
                DayGardenResponse.builder()
                        .contributeDate(LocalDate.of(2024,1,2))
                        .contributeCount(1)
                        .build(),
                DayGardenResponse.builder()
                        .contributeDate(LocalDate.of(2024,1,7))
                        .contributeCount(5)
                        .build()
        );

        //when
        when(gardenQueryService.getGardens(any())).thenReturn(gardenResponse);

        //then
        mockMvc.perform(get("/garden/{teamId}", 1))
                .andExpect(status().isOk())
                .andDo(document("garden-get",
                        pathParameters(parameterWithName("teamId").description("팀 id")),
                        responseFields(
                                stringFieldWithPath("[].contributeDate", "기여된 날짜"),
                                numberFieldWithPath("[].contributeCount", "그날의 기여도(기여된 횟수)")
                        )));
    }

}
