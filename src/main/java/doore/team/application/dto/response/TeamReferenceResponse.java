package doore.team.application.dto.response;


public record TeamReferenceResponse(
        Long id,

        String name,

        String description,

        String imageUrl
) {
}
