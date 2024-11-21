package doore.member.application.convenience;

import static doore.member.domain.TeamRoleType.ROLE_팀원;
import static doore.member.domain.TeamRoleType.ROLE_팀장;

import doore.member.domain.TeamRole;
import doore.member.domain.repository.TeamRoleRepository;
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
}
