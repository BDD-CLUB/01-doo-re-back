package doore.team.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.helper.IntegrationTest;
import doore.team.application.dto.request.TeamCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TeamControllerTest extends IntegrationTest {

    @Test
    @DisplayName("팀 생성은 성공한다. [성공]")
    public void 팀_생성은_성공한다_성공() throws Exception {
        final TeamCreateRequest request = new TeamCreateRequest("BDD", "개발 동아리 입니다.");

        callPostApi("/teams", request).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("필수값을 설정하지 않을 경우 팀 생성은 실패한다. [실패]")
    public void 필수값을_설정하지_않을_경우_팀_생성은_실패한다_실패() throws Exception {
        final TeamCreateRequest request = new TeamCreateRequest(null, "개발 동아리 입니다.");

        callPostApi("/teams", request).andExpect(status().isBadRequest());
    }
}
