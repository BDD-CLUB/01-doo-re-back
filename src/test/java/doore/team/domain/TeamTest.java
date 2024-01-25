package doore.team.domain;

import static org.assertj.core.api.Assertions.assertThat;

import doore.team.TeamFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TeamTest {

    @Test
    @DisplayName("팀의 정보는 변경된다. [성공]")
    public void 팀의_정보는_변경된다_성공() {
        //given
        final Team team = TeamFixture.team();

        //when
        final String changeName = "비디디";
        final String changeDescription = "개발을 위한 동아리 입니다.";
        team.update(changeName, changeDescription);

        //then
        assertThat(team.getName()).isEqualTo(changeName);
        assertThat(team.getDescription()).isEqualTo(changeDescription);
    }
}
