package doore.team.application.dto.response;

import doore.garden.application.dto.response.DayGardenResponse;
import java.util.List;

public record TeamRankResponse(
        int point,
        TeamReferenceResponse teamReferenceResponse,
        List<DayGardenResponse> teamGardenResponse
) {
}
