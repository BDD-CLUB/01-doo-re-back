package doore.team.application.dto.response;


import doore.team.domain.Team;

public record TeamReferenceResponse(
        Long id,

        String name,

        String description,

        String imageUrl
) {
    public static TeamReferenceResponse from(final Team team) {
        return new TeamReferenceResponse(team.getId(), team.getName(), team.getDescription(), team.getImageUrl());
    }
}
