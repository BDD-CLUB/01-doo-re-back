package doore.member.application;

import doore.login.application.dto.response.GoogleAccountProfileResponse;
import doore.member.domain.Member;
import doore.member.domain.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;

    // TODO: 1/23/24 추후 확장성 관련해서 논의
    public Member findOrCreateMemberBy(final GoogleAccountProfileResponse profile) {
        return memberRepository.findByGoogleId(profile.getId())
                .orElse(memberRepository.save(
                        new Member(profile.getName(), profile.getId(), profile.getEmail(), profile.getPicture()))
                );
    }

}
