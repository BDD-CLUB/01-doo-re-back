package doore.member.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.application.dto.response.MemberAndMyTeamsAndStudiesResponse;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import doore.team.application.TeamQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {
    private final MemberRepository memberRepository;
    private final TeamQueryService teamQueryService;

    public MemberAndMyTeamsAndStudiesResponse getSideBarInfo(final Long memberId, final Long tokenMemberId) {
        validateMember(memberId);
        checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);
        final Member member = memberRepository.findById(tokenMemberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        return MemberAndMyTeamsAndStudiesResponse.of(member, teamQueryService.findMyTeamsAndStudies(memberId));
    }

    private void validateMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void checkSameMemberIdAndTokenMemberId(final Long memberId, final Long tokenMemberId) {
        if (!memberId.equals(tokenMemberId)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
