package doore.member.application;

import static doore.member.exception.MemberExceptionType.CANNOT_DELETE_TEAM_LEADER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_TEAM;

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

    public void deleteMemberTeam(final Long teamId, final Long memberId, final Long teamLeaderId) {
        validateExistTeam(teamId);
        validateTeamLeader(teamLeaderId, teamId);
        validateTeamMember(memberId, teamId);
        memberTeamRepository.deleteByTeamIdAndMemberId(teamId, memberId);
    }

    private void validateTeamMember(final Long memberId, final Long teamId) {
        TeamRole teamRole = teamRoleRepository.findTeamRoleByTeamIdAndMemberId(teamId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_TEAM));
        if (teamRole.getTeamRoleType().equals(TeamRoleType.ROLE_팀장)) {
            throw new MemberException(CANNOT_DELETE_TEAM_LEADER);
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
