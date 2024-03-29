package doore.member.domain.repository;

import doore.member.domain.TeamRole;
import doore.member.domain.TeamRoleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRoleRepository extends JpaRepository<TeamRole, Long> {
    Optional<TeamRole> findTeamRoleByTeamIdAndMemberId(Long teamId, Long memberId);
    Optional<TeamRole> findTeamRoleByTeamIdAndTeamRoleType(Long teamId, TeamRoleType teamRoleType);
}
