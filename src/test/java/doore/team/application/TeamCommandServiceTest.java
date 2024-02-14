package doore.team.application;

import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.helper.IntegrationTest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.exception.TeamException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamCommandServiceTest extends IntegrationTest {

    @Autowired
    private TeamCommandService teamCommandService;

    @Test
    @DisplayName("[실패] 찾을 수 없는 팀은 팀 정보를 수정할 수 없다.")
    public void updateTeam_찾을_수_없는_팀은_팀_정보를_수정할_수_없다_실패() {
        //given
        final Long invalidId = 0L;
        TeamUpdateRequest request = new TeamUpdateRequest("asdf", "asdf");

        //when & then
        assertThatThrownBy(() -> {
            teamCommandService.updateTeam(invalidId, request);
        }).isInstanceOf(TeamException.class)
                .hasMessage(NOT_FOUND_TEAM.errorMessage());
    }
}
