package doore.team.application;

import doore.study.application.dto.response.StudyNameResponse;
import doore.study.domain.repository.StudyRepository;
import doore.team.application.dto.response.MyTeamsAndStudiesResponse;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import doore.team.domain.TeamRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final StudyRepository studyRepository;

    public List<TeamReferenceResponse> findMyTeams(final Long memberId) {
        return teamRepository.findAllByMemberId(memberId)
                .stream()
                .map(TeamReferenceResponse::from)
                .toList();
    }

    public List<MyTeamsAndStudiesResponse> findMyTeamsAndStudies(Long memberId) {
        List<MyTeamsAndStudiesResponse> myTeamsAndStudiesResponses = new ArrayList<>();
        List<Team> myTeams = teamRepository.findAllByMemberId(memberId);

        for (Team myTeam : myTeams) {
            List<StudyNameResponse> studyNameResponses =
                    studyRepository.findAllByTeamId(myTeam.getId()).stream()
                            .map(StudyNameResponse::from)
                            .toList();
            MyTeamsAndStudiesResponse myTeamsAndStudiesResponse =
                    new MyTeamsAndStudiesResponse(myTeam.getId(), myTeam.getName(), studyNameResponses);
            myTeamsAndStudiesResponses.add(myTeamsAndStudiesResponse);
        }
        return myTeamsAndStudiesResponses;
    }
}
