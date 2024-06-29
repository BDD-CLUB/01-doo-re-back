package doore.team.application.dto.response;

import doore.member.application.dto.response.MemberReferenceResponse;
import doore.study.application.dto.response.StudyNameResponse;
import java.util.List;

public record MyTeamsAndStudiesResponse(
        Long teamId,
        String teamName,
        List<StudyNameResponse> teamStudies,
        MemberReferenceResponse memberReference
) {
}
