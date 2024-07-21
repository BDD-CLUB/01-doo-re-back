package doore.member.application.convenience;

import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_TEAM;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.domain.TeamRole;
import doore.member.domain.TeamRoleType;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamRoleValidateAccessPermission {
    private final TeamRoleRepository teamRoleRepository;

    public void validateTeamMember(final Long deleteMemberId, final Long teamId) {
        TeamRole teamRole = teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, deleteMemberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
        if (teamRole.getTeamRoleType().equals(TeamRoleType.ROLE_팀장)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    public void validateTeamLeader(Long teamLeaderId, Long teamId) {
        teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, teamLeaderId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
    }

    public void validateExistTeamLeaderAndTeamMember(final Long teamId, final Long memberId) {
        final TeamRole teamRole = teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
        if (!(teamRole.getTeamRoleType().equals(ROLE_팀장) || teamRole.getTeamRoleType().equals(ROLE_팀원))){
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
