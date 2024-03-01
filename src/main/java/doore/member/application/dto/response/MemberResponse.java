package doore.member.application.dto.response;

import doore.member.domain.Member;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record MemberResponse(
        Long id,
        String name,
        String email,
        String imageUrl,
        String role,  // 권한 기능 구현 후 수정 예정
        Boolean isDeleted
) {

    public static List<MemberResponse> of(final List<Member> members, Map<Member, String> roleOfMembers) {
        return members.stream()
                .map(member -> new MemberResponse(
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
