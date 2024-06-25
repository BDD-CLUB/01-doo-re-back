package doore.member.application.dto.response;

import doore.member.domain.Member;
import doore.member.domain.TeamRoleType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record TeamMemberResponse(
        Long id,
        String name,
        String email,
        String imageUrl,
        TeamRoleType teamRole,
        Boolean isDeleted
) {

    public static List<TeamMemberResponse> of(final List<Member> members, final Map<Member, TeamRoleType> roleOfMembers) {
        return members.stream()
                .map(member -> new TeamMemberResponse(
                        member.getId(),
                        member.getName(),
                        member.getEmail(),
                        member.getImageUrl(),
                        roleOfMembers.get(member),
                        member.getIsDeleted()
                ))
                .collect(Collectors.toList());
    }
}
