package doore.team.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("select t from Team t join MemberTeam mt on mt.teamId=t.id where mt.member.id=:memberId")
    List<Team> findAllByMemberId(final Long memberId);

}
