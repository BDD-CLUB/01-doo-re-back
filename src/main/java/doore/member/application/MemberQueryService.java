package doore.member.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import doore.member.application.convenience.MemberConvenience;
import doore.member.application.convenience.MemberValidateAccessPermission;
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

    private final MemberConvenience memberConvenience;

    private final MemberValidateAccessPermission memberValidateAccessPermission;

    public MemberAndMyTeamsAndStudiesResponse getSideBarInfo(final Long memberId, final Long tokenMemberId) {
        memberValidateAccessPermission.validateExistMember(memberId);
        memberConvenience.checkSameMemberIdAndTokenMemberId(memberId, tokenMemberId);
        final Member member = memberRepository.findById(tokenMemberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        return MemberAndMyTeamsAndStudiesResponse.of(member, teamQueryService.getMyTeamsAndStudies(memberId));
    }
}
