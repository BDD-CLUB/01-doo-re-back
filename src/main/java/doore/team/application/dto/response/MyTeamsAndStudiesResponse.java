package doore.team.application.dto.response;

import doore.member.application.dto.response.MemberReferenceResponse;
import doore.study.application.dto.response.StudyNameResponse;
import doore.team.domain.Team;
import java.util.List;
import lombok.Builder;

@Builder
public record MyTeamsAndStudiesResponse(
        Long teamId,
        String teamName,
        List<StudyNameResponse> teamStudies,
        MemberReferenceResponse memberReference
) {
    public static MyTeamsAndStudiesResponse of(final Team team, final List<StudyNameResponse> teamStudies,
                                             final MemberReferenceResponse memberReference) {
        return MyTeamsAndStudiesResponse.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .teamStudies(teamStudies)
                .memberReference(memberReference)
                .build();
    }
}
