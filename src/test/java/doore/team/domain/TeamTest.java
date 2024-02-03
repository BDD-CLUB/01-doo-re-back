package doore.team.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.team.TeamFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TeamTest {

    @Test
    @DisplayName("[성공] 팀의 정보는 변경된다.")
    public void update_팀의_정보는_변경된다_성공() {
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

    @Test
    @DisplayName("필수값이 Null일 경우, 예외를 발생시킨다.")
    public void builder_필수값이_Null일_경우_예외를_발생시킨다() {
        //when & then
        assertThatThrownBy(() -> {
            Team.builder()
                    .name("BDD")
                    .imageUrl("asdf")
                    .build();
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null일 수 없습니다.");
    }
}
