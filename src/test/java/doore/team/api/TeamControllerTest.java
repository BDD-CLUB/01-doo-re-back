package doore.team.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import doore.helper.IntegrationTest;
import doore.team.application.dto.request.TeamCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TeamControllerTest extends IntegrationTest {

    @Test
    @DisplayName("[실패] 필수값을 설정하지 않을 경우 팀 생성은 실패한다.")
    public void createTeam_필수값을_설정하지_않을_경우_팀_생성은_실패한다_실패() throws Exception {
        final TeamCreateRequest request = new TeamCreateRequest(null, "개발 동아리 입니다.");

        callPostApi("/teams", request).andExpect(status().isBadRequest());
    }
    // TODO: 3/22/24 자기 자신이 아닌 회원의 팀 목록을 조회할 경우 예외를 반환한다.
}
