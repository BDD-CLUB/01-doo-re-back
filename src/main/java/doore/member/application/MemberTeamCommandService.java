package doore.member.application;

import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_TEAM;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.member.domain.TeamRole;
import doore.member.domain.TeamRoleType;
import doore.member.domain.repository.MemberTeamRepository;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
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
    private final TeamRoleRepository teamRoleRepository;
    private final TeamRepository teamRepository;

    public void deleteMemberTeam(final Long teamId, final Long deleteMemberId, final Long teamLeaderId) {
        validateExistTeam(teamId);
        validateTeamLeader(teamLeaderId, teamId);
        validateTeamMember(deleteMemberId, teamId);
        memberTeamRepository.deleteByTeamIdAndMemberId(teamId, deleteMemberId);
    }

    private void validateTeamMember(final Long deleteMemberId, final Long teamId) {
        TeamRole teamRole = teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, deleteMemberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
        if (teamRole.getTeamRoleType().equals(TeamRoleType.ROLE_팀장)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    private void validateExistTeam(final Long teamId) {
        teamRepository.findById(teamId).orElseThrow(() -> new TeamException(TeamExceptionType.NOT_FOUND_TEAM));
    }

    private void validateTeamLeader(Long teamLeaderId, Long teamId) {
        teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, teamLeaderId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
    }

}
