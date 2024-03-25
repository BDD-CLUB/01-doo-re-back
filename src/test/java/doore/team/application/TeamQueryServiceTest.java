package doore.team.application;

import static doore.team.TeamFixture.team;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import doore.document.DocumentFixture;
import doore.document.domain.DocumentGroupType;
import doore.helper.IntegrationTest;
import doore.team.application.dto.response.TeamRankResponse;
import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TeamQueryServiceTest extends IntegrationTest {

    @Autowired
    TeamQueryService teamQueryService;

    @Autowired
    TeamRepository teamRepository;

    Long ranking1stTeamId;
    Long ranking2ndTeamId;

    @BeforeEach
    void setUp() {
        Team team = team();
        teamRepository.save(team);
        ranking1stTeamId = team.getId();

        Team otherTeam = team();
        teamRepository.save(otherTeam);
        ranking2ndTeamId = otherTeam.getId();

        DocumentFixture.builder().groupType(DocumentGroupType.TEAM).groupId(ranking1stTeamId).buildDocument();
        DocumentFixture.builder().groupType(DocumentGroupType.TEAM).groupId(ranking1stTeamId).buildDocument();
        DocumentFixture.builder().groupType(DocumentGroupType.TEAM).groupId(ranking2ndTeamId).buildDocument();
    }

    @Test
    @DisplayName("정상적으로_팀랭킹을_불러올수_있다.")
    public void getTeamRanks_정상적으로_팀랭킹을_불러올수_있다_성공() throws Exception {
        //given

        //when
        List<TeamRankResponse> teamRankResponses = teamQueryService.getTeamRanks();

        //then
        assertAll(
                () -> assertThat(teamRankResponses).hasSize(2),
                () -> assertEquals(1, teamRankResponses.get(0).rank()),
                () -> assertEquals(ranking1stTeamId, teamRankResponses.get(0).teamReferenceResponse().id()),
                () -> assertEquals(2, teamRankResponses.get(1).rank()),
                () -> assertEquals(ranking2ndTeamId, teamRankResponses.get(1).teamReferenceResponse().id())
        );
    }

    @Test
    @DisplayName("동일한 포인트를 가진 팀은 동일한 랭킹을 가진다.")
    public void getTeamRanks_동일한_포인트를_가진_팀은_동일한_랭킹을_가진다_성공() throws Exception {
        //given
        Team otherRanking2ndTeam = team();
        teamRepository.save(otherRanking2ndTeam);
        Long otherRanking2ndTeamId = otherRanking2ndTeam.getId();
        DocumentFixture.builder().groupType(DocumentGroupType.TEAM).groupId(otherRanking2ndTeamId).buildDocument();

        Team otherRanking1stTeam = team();
        teamRepository.save(otherRanking1stTeam);
        Long otherRanking1stTeamId = otherRanking1stTeam.getId();
        DocumentFixture.builder().groupType(DocumentGroupType.TEAM).groupId(otherRanking1stTeamId).buildDocument();
        DocumentFixture.builder().groupType(DocumentGroupType.TEAM).groupId(otherRanking1stTeamId).buildDocument();

        //when
        List<TeamRankResponse> teamRankResponses = teamQueryService.getTeamRanks();

        //then
        assertAll(
                () -> assertThat(teamRankResponses).hasSize(4),
                () -> assertSame(teamRankResponses.get(0).rankPoint(), teamRankResponses.get(1).rankPoint()),
                () -> assertEquals(1, teamRankResponses.get(0).rank()),
                () -> assertEquals(ranking1stTeamId, teamRankResponses.get(0).teamReferenceResponse().id()),
                () -> assertEquals(1, teamRankResponses.get(1).rank()),
                () -> assertEquals(otherRanking1stTeamId, teamRankResponses.get(1).teamReferenceResponse().id()),

                () -> assertSame(teamRankResponses.get(2).rankPoint(), teamRankResponses.get(3).rankPoint()),
                () -> assertEquals(2, teamRankResponses.get(2).rank()),
                () -> assertEquals(ranking2ndTeamId, teamRankResponses.get(2).teamReferenceResponse().id()),
                () -> assertEquals(2, teamRankResponses.get(3).rank()),
                () -> assertEquals(otherRanking2ndTeamId, teamRankResponses.get(3).teamReferenceResponse().id())
        );
    }
}
