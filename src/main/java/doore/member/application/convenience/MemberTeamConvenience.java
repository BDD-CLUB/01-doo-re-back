package doore.member.application.convenience;

import static doore.member.exception.MemberExceptionType.ALREADY_JOIN_TEAM_MEMBER;

import doore.member.domain.Member;
import doore.member.domain.MemberTeam;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.exception.MemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberTeamConvenience {
    private final MemberTeamRepository memberTeamRepository;

    public void duplicateCheckTeamMember(final Long teamId, final Long memberId) {
        if (memberTeamRepository.existsByTeamIdAndMemberId(teamId, memberId)) {
            throw new MemberException(ALREADY_JOIN_TEAM_MEMBER);
        }
    }

    public void assignMemberTeam(final Member member, final Long teamId) {
        memberTeamRepository.save(MemberTeam.builder()
                .member(member)
                .isDeleted(false)
                .teamId(teamId)
                .build());
    }

    public void deleteAllMemberTeams(final Long memberId) {
        final List<MemberTeam> memberTeams = memberTeamRepository.findAllByMemberId(memberId);
        memberTeamRepository.deleteAll(memberTeams);
    }
}
