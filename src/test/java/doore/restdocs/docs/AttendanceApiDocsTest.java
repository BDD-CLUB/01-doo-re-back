package doore.restdocs.docs;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.attendance.api.AttendanceController;
import doore.restdocs.RestDocsTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(AttendanceController.class)
public class AttendanceApiDocsTest extends RestDocsTest {

    @Test
    @DisplayName("[성공] 정상적으로 출석할 수 있다.")
    public void createAttendance_정상적으로_출석할_수_있다_성공() throws Exception {
        mockMvc.perform(post("/attendances")
                        .header("Authorization", "1"))
                .andExpect(status().isCreated())
                .andDo(document("attendance-create",
                        requestHeaders(headerWithName("Authorization").description("member id"))
                ));
    }
}
