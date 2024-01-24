package doore.team.domain;

import static org.assertj.core.api.Assertions.assertThat;

import doore.helper.RepositorySliceTest;
import doore.team.TeamFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamRepositoryTest extends RepositorySliceTest {

    @Autowired
    private TeamRepository teamRepository;

    @Test
    @DisplayName("팀 삭제시 업데이트 쿼리가 적용된다.")
    public void 팀_삭제시_업데이트_쿼리가_적용된다() {
        //given
        Team team = TeamFixture.team();
        teamRepository.save(team);
        em.flush();
        em.clear();

        //when & then
        teamRepository.delete(team);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("삭제된 팀원은 조회되지 않는다. [성공]")
    public void 삭제된_팀원은_조회되지_않는다_성공() {
        //given
        Team team = TeamFixture.deletedTeam();
        teamRepository.save(team);
        em.flush();
        em.clear();

        //when
        Optional<Team> result = teamRepository.findById(team.getId());

        //then
        assertThat(result).isEmpty();
    }
}
