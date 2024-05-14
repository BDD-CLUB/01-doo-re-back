package doore.team.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.TeamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TeamQueryService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    public List<TeamReferenceResponse> findMyTeams(final Long memberId) {
        validateMember(memberId);
        return teamRepository.findAllByMemberId(memberId)
                .stream()
                .map(TeamReferenceResponse::from)
                .toList();
    }

    private void validateMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
