package doore.member.domain.repository;

import doore.member.domain.TeamRole;
import doore.member.domain.TeamRoleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRoleRepository extends JpaRepository<TeamRole, Long> {
    Optional<TeamRole> findTeamRoleByTeamIdAndMemberId(Long teamId, Long memberId);
    Optional<TeamRole> findTeamRoleByTeamIdAndTeamRoleType(Long teamId, TeamRoleType teamRoleType);
    Optional<TeamRole> findTeamRoleByMemberId(Long memberId);
    @Query("SELECT tr.memberId FROM TeamRole tr WHERE tr.teamId = :teamId AND tr.teamRoleType = 'ROLE_팀장'")
    Long findLeaderIdByTeamId(Long teamId);
}
