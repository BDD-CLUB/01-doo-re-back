package doore.team.application;

import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;
import static doore.team.exception.TeamExceptionType.NOT_MATCH_LINK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import doore.helper.IntegrationTest;
import doore.team.TeamFixture;
import doore.team.application.dto.request.TeamInviteCodeRequest;
import doore.team.application.dto.request.TeamUpdateRequest;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamCommandServiceTest extends IntegrationTest {

    @Autowired
    private TeamCommandService teamCommandService;

    @Autowired
    private TeamRepository teamRepository;

    private Long teamId;

    @BeforeEach
    void setUp() {
        teamId = teamRepository.save(TeamFixture.team()).getId();
    }

    @AfterEach
    void flushAll() {
        redisUtil.flushAll();
    }

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

    @Test
    @Disabled // TODO: 2/14/24 수정 
    @DisplayName("[성공] 초대코드는 생성된다.")
    public void generateTeamInviteCode_초대코드는_생성된다_성공() {

        //when
        var teamInviteLinkResponse = teamCommandService.generateTeamInviteCode(teamId);

        //then
        Optional<String> data = redisUtil.getData("teamId:%d".formatted(teamId), String.class);
        assertThat(data).isNotEmpty();
        assertThat(data.get()).isEqualTo(teamInviteLinkResponse.code());
    }

    @Test
    @DisplayName("[성공] 이미 존재하는 초대코드가 있을 경우 초대코드를 반환한다.")
    public void generateTeamInviteCode_이미_존재하는_초대코드가_있을_경우_초대코드를_반환한다_성공() {
        //given
        var createdCode = teamCommandService.generateTeamInviteCode(teamId).code();

        //when
        var getCode = teamCommandService.generateTeamInviteCode(teamId).code();

        //then
        assertThat(createdCode).isEqualTo(getCode);
    }

    @Test
    @Disabled // TODO: 2/14/24 수정
    @DisplayName("[성공] 초대코드와 유저코드가 일치하면 팀 가입은 성공한다.")
    public void joinTeam_초대코드와_유저코드가_일치하면_팀_가입은_성공한다_성공() {
        //given
        var createdCode = teamCommandService.generateTeamInviteCode(teamId).code();

        //when & then
        assertDoesNotThrow(() -> teamCommandService.joinTeam(teamId, new TeamInviteCodeRequest(createdCode)));
    }

    @Test
    @DisplayName("[실패] 초대코드와 유저코드가 일치하지 않으면 팀 가입은 실패한다.")
    public void joinTeam_초대코드와_유저코드가_일치하지_않으면_팀_가입은_실패한다() {
        //given
        teamCommandService.generateTeamInviteCode(teamId).code();

        //when & then
        assertThatThrownBy(() -> {
            teamCommandService.joinTeam(teamId, new TeamInviteCodeRequest("invalid code"));
        }).isInstanceOf(TeamException.class)
                .hasMessage(NOT_MATCH_LINK.errorMessage());
    }
}
