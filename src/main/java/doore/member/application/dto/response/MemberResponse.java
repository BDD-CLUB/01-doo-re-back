package doore.member.application.dto.response;

import doore.member.domain.Member;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String imageUrl;
    private final String role;  // 권한 기능 구현 후 수정 예정
    private final boolean isDeleted;

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
