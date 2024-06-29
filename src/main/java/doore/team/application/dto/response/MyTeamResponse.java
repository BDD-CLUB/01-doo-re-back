package doore.team.application.dto.response;

import doore.member.application.dto.response.MemberReferenceResponse;
import doore.member.domain.Member;
import doore.team.domain.Team;
import lombok.Builder;

@Builder
public record MyTeamResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        MemberReferenceResponse memberReference
) {
    public static MyTeamResponse of(final Team team, final Member member) {
        return MyTeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .imageUrl(team.getImageUrl())
                .memberReference(MemberReferenceResponse.from(member))
                .build();
    }
}

