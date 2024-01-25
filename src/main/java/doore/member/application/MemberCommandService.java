package doore.member.application;

import doore.login.application.dto.response.GoogleAccountProfileResponse;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;

    // TODO: 1/23/24 추후 소셜 로그인 플랫폼이 늘어나는 경우의 확장성 관련해서 논의
    public Member findOrCreateMemberBy(final GoogleAccountProfileResponse profile) {
        return memberRepository.findByGoogleId(profile.id()).orElseGet(() -> memberRepository.save(
                Member.builder().name(profile.name()).googleId(profile.id()).email(profile.email())
                        .imageUrl(profile.picture()).build()));
    }
}
