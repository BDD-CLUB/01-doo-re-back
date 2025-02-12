package doore.team.domain.repository;

import doore.team.domain.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t JOIN MemberTeam mt ON mt.teamId = t.id " +
            "WHERE mt.member.id = :memberId AND mt.isDeleted = false")
    List<Team> findAllByMemberId(final Long memberId);

}
