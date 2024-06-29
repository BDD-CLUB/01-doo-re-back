package doore.team.application.dto.response;

import doore.member.application.dto.response.MemberReferenceResponse;
import doore.member.domain.Member;
import doore.team.domain.Team;
import lombok.Builder;

@Builder
public record TeamResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        MemberReferenceResponse memberReference,
        long attendanceRatio
) {
    public static TeamResponse of(final Team team, final long attendanceRatio) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .imageUrl(team.getImageUrl())
                .attendanceRatio(attendanceRatio)
                .build();
    }

    public static TeamResponse of(final Team team, final Member member) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .imageUrl(team.getImageUrl())
                .memberReference(MemberReferenceResponse.from(member))
                .build();
    }
}
