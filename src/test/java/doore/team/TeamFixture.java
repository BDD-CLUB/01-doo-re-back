package doore.team;

import doore.team.domain.Team;

public class TeamFixture {

    public static Team team() {
        return Team.builder()
                .name("BDD")
                .description("개발 동아리 입니다.")
                .imageUrl("url")
                .build();
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
