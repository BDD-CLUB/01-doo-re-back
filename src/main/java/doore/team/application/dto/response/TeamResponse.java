package doore.team.application.dto.response;

import doore.team.domain.Team;
import lombok.Builder;

@Builder
public record TeamResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long attendanceRatio
) {
    public static TeamResponse of(final Team team, final Long attendanceRatio) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .imageUrl(team.getImageUrl())
                .attendanceRatio(attendanceRatio)
                .build();
    }
}
