package doore.team;

import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TeamFixture {
    private static TeamRepository teamRepository;

    @Autowired
    public TeamFixture(TeamRepository teamRepository) {
        TeamFixture.teamRepository = teamRepository;
    }

    public static Team team() {
        Team team = Team.builder()
                .name("BDD")
                .description("개발 동아리 입니다.")
                .imageUrl("url")
                .build();
        return teamRepository.save(team);
    }

    public static Team deletedTeam() {
        return Team.builder()
                .name("BDD")
                .description("개발 동아리 입니다.")
                .imageUrl("url")
                .isDeleted(true)
                .build();
    }
}
