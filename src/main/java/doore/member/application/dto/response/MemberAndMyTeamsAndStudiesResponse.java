package doore.member.application.dto.response;

import doore.member.domain.Member;
import doore.team.application.dto.response.MyTeamsAndStudiesResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberAndMyTeamsAndStudiesResponse(
        Long id,
        String name,
        String imageUrl,
        List<MyTeamsAndStudiesResponse> myTeamsAndStudies
) {
    public static MemberAndMyTeamsAndStudiesResponse of(final Member member,
                                                        List<MyTeamsAndStudiesResponse> myTeamsAndStudiesResponse) {
        return MemberAndMyTeamsAndStudiesResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .imageUrl(member.getImageUrl())
                .myTeamsAndStudies(myTeamsAndStudiesResponse)
                .build();
    }
}
