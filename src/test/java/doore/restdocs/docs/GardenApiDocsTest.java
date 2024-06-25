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
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GardenApiDocsTest extends RestDocsTest {

    @Test
    @DisplayName("[성공] 팀의 올해 텃밭을 조회한다.")
    public void getAllGarden_팀의_올해_텃밭을_생성한다() throws Exception {
        //given
        final List<DayGardenResponse> fullGardenResponse = List.of(
                DayGardenResponse.builder()
                        .dayOfYear(0)
                        .weekOfYear(0)
                        .dayOfWeek(0)
                        .contributeCount(2)
                        .build(),
                DayGardenResponse.builder()
                        .dayOfYear(1)
                        .weekOfYear(0)
                        .dayOfWeek(1)
                        .contributeCount(1)
                        .build(),
                DayGardenResponse.builder()
                        .dayOfYear(7)
                        .weekOfYear(1)
                        .dayOfWeek(0)
                        .contributeCount(5)
                        .build()
        );

        //when
        when(gardenQueryService.getAllGarden(any())).thenReturn(fullGardenResponse);

        //then
        mockMvc.perform(get("/garden/{teamId}", 1))
                .andExpect(status().isOk())
                .andDo(document("garden-get-all",
                        pathParameters(parameterWithName("teamId").description("팀 id")),
                        responseFields(
                                numberFieldWithPath("[].dayOfYear", "1년 중 몇번째 날인가(0~365)"),
                                numberFieldWithPath("[].weekOfYear", "1년 중 몇번째 주인가(0~52)"),
                                numberFieldWithPath("[].dayOfWeek", "1주 중 몇번째 요일인가(월요일부터 시작, 0~7)"),
                                numberFieldWithPath("[].contributeCount", "그날의 기여도(기여된 횟수)")
                        )));
    }

}
