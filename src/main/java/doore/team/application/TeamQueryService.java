package doore.team.application;

import doore.document.domain.repository.DocumentRepository;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import doore.team.application.dto.response.TeamRankResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import doore.team.domain.repository.TeamRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final ParticipantCurriculumItemRepository participantCurriculumItemRepository;
    private final DocumentRepository documentRepository;

    public List<TeamRankResponse> getTeamRanks() {
        List<TeamRankPoint> teamRankPoints = new ArrayList<>();

        calculatePoint(teamRankPoints);
        teamRankPoints.sort(Comparator.comparing(TeamRankPoint::getRankPoint, Comparator.reverseOrder()));
        return provideRank(teamRankPoints);
    }

    private List<TeamRankResponse> provideRank(List<TeamRankPoint> teamRankPoints) {
        final List<TeamRankResponse> teamRankResponses = new ArrayList<>();
        int prevRank = 0;
        int prevRankPoint = 0;
        for (TeamRankPoint teamRankPoint : teamRankPoints) {
            Team team = teamRankPoint.getTeam();
            TeamReferenceResponse teamReferenceResponse =
                    new TeamReferenceResponse(team.getId(), team.getName(), team.getDescription(), team.getImageUrl());
            if (!isSamePoint(prevRankPoint, teamRankPoint.getRankPoint())) {
                prevRank++;
                prevRankPoint = teamRankPoint.getRankPoint();
            }

            TeamRankResponse teamRankResponse =
                    new TeamRankResponse(prevRank, teamRankPoint.getRankPoint(), teamReferenceResponse);
            teamRankResponses.add(teamRankResponse);
        }
        return teamRankResponses;
    }

    private boolean isSamePoint(int prevRankPoint, int curRankPoint) {
        return prevRankPoint == curRankPoint;
    }

    private void calculatePoint(List<TeamRankPoint> teamRankPoints) {
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            Integer RankPointOfDay = participantCurriculumItemRepository.getTodayCheckedCurriculumItemFromTeam(team.getId())
                    + documentRepository.getTodayUploadedDocumentFromTeam(team.getId());

            TeamRankPoint teamRankPoint = new TeamRankPoint(team, RankPointOfDay);
            teamRankPoints.add(teamRankPoint);
        }
    }


}
