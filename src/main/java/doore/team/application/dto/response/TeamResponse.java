package doore.team.application.dto.response;

import doore.team.domain.Team;
import lombok.Builder;

@Builder
public record TeamResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long teamLeaderId
) {
    public static TeamResponse of(final Team team, final Long teamLeaderId) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .imageUrl(team.getImageUrl())
                .teamLeaderId(teamLeaderId)
                .build();
    }
}
