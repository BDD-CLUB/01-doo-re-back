package doore.member.application;

import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.domain.repository.MemberTeamRepository;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
import doore.team.exception.TeamExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberTeamCommandService {
    private final MemberTeamRepository memberTeamRepository;
    private final TeamRepository teamRepository;

    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;

    public void deleteMemberTeam(final Long teamId, final Long deleteMemberId, final Long teamLeaderId) {
        validateExistTeam(teamId);
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, teamLeaderId);
        teamRoleValidateAccessPermission.validateExistMemberTeam(teamId, deleteMemberId);
        memberTeamRepository.deleteByTeamIdAndMemberId(teamId, deleteMemberId);
    }

    private void validateExistTeam(final Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
    }
}
