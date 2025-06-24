package doore.member.application.convenience;

import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;
import static doore.member.exception.MemberExceptionType.CANNOT_DELETE_TEAM_LEADER;

import doore.member.domain.TeamRole;
import doore.member.domain.repository.TeamRoleRepository;
import doore.member.exception.MemberException;
import doore.team.domain.Team;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamRoleConvenience {
    private final TeamRoleRepository teamRoleRepository;

    public void assignTeamLeaderRole(final Long teamId, final Long memberId) {
        teamRoleRepository.save(TeamRole.builder()
                .teamId(teamId)
                .teamRoleType(ROLE_팀장)
                .memberId(memberId)
                .build());
    }

    public void assignTeamMemberRole(final Long teamId, final Long memberId) {
        teamRoleRepository.save(TeamRole.builder()
                .teamId(teamId)
                .teamRoleType(ROLE_팀원)
                .memberId(memberId)
                .build());
    }

    public void deleteByTeamIdAndMemberId(final Long teamId, final Long memberId) {
        teamRoleRepository.deleteByTeamIdAndMemberId(teamId, memberId);
    }

    public void isTeamLeader(final List<Team> teams, final Long memberId) {
        final List<String> leaderTeamNames = teams.stream()
                .filter(team -> teamRoleRepository.existsByTeamIdAndMemberIdAndTeamRoleType(
                        team.getId(), memberId, ROLE_팀장))
                .map(Team::getName)
                .toList();

        if (!leaderTeamNames.isEmpty()) {
            String joinedNames = String.join(", ", leaderTeamNames);
            String formattedMessage = String.format(CANNOT_DELETE_TEAM_LEADER.errorMessage(), joinedNames);
            throw new MemberException(CANNOT_DELETE_TEAM_LEADER, formattedMessage);
        }
    }

    public void deleteAllTeamRoles(final Long memberId) {
        final List<TeamRole> teamRoles = teamRoleRepository.findAllByMemberId(memberId);
        teamRoleRepository.deleteAll(teamRoles);
    }
}
