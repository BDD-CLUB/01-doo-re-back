package doore.member.application.dto.response;

import doore.member.domain.Member;

public record MemberReferenceResponse(
        String name,
        String imageUrl
) {
    public static MemberReferenceResponse from(final Member member) {
        return new MemberReferenceResponse(member.getName(), member.getImageUrl());
    }
}
