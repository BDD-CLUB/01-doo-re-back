package doore.team.application.dto.response;

public record TeamRankResponse(
        Integer rank,
        Integer rankPoint,
        TeamReferenceResponse teamReferenceResponse
) {
}
