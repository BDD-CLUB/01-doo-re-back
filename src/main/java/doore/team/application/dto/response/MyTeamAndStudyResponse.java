package doore.team.application.dto.response;

import doore.study.application.dto.response.totalStudyResponse.StudySimpleResponse;
import java.util.List;

public record MyTeamAndStudyResponse(
        List<TeamReferenceResponse> teamReferenceResponses,
        List<StudySimpleResponse> studySimpleResponses
) {

}
