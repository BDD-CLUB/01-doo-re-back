package doore.member.application.convenience;

import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberValidateAccessPermission {
    private final MemberRepository memberRepository;

    public void validateExistMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(UNAUTHORIZED));
    }

    public Member getValidateExistMember(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(UNAUTHORIZED));
    }
}
