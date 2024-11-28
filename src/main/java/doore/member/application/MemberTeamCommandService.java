package doore.member.application;

import doore.member.application.convenience.TeamRoleConvenience;
import doore.member.application.convenience.TeamRoleValidateAccessPermission;
import doore.member.domain.repository.MemberTeamRepository;
import doore.team.application.convenience.TeamValidateAccessPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberTeamCommandService {
    private final MemberTeamRepository memberTeamRepository;

    private final TeamRoleValidateAccessPermission teamRoleValidateAccessPermission;
    private final TeamValidateAccessPermission teamValidateAccessPermission;

    private final TeamRoleConvenience teamRoleConvenience;

    public void deleteMemberTeam(final Long teamId, final Long deleteMemberId, final Long teamLeaderId) {
        teamValidateAccessPermission.validateExistTeam(teamId);
        teamRoleValidateAccessPermission.validateExistTeamLeader(teamId, teamLeaderId);
        teamRoleValidateAccessPermission.validateExistMemberTeam(teamId, deleteMemberId);
        memberTeamRepository.deleteByTeamIdAndMemberId(teamId, deleteMemberId);
        teamRoleConvenience.deleteByTeamIdAndMemberId(teamId, deleteMemberId);
    }
}
