package doore.garden.domain.repository;

import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GardenRepository extends JpaRepository<Garden,Long> {
    void deleteByContributionIdAndType(Long contributionId, GardenType gardenType);
    @Query("SELECT g FROM Garden g WHERE g.teamId = :teamId AND YEAR(g.contributedDate) = YEAR(CURRENT_DATE)")
    List<Garden> findAllOfThisYearByTeamIdOrderByContributedDateAsc(Long teamId); //올해의 데이터만 가져온다.

    @Query("SELECT g FROM Garden g WHERE g.teamId = :teamId AND g.contributedDate = CURRENT_DATE")
    List<Garden> findTodayGardenByTeamId(Long teamId); //오늘의 데이터만 가져온다.

    @Query("SELECT g FROM Garden g WHERE g.teamId = :teamId AND YEARWEEK(g.contributedDate, 1) = YEARWEEK(CURRENT_DATE, 1)")
    List<Garden> findThisWeekGardenByTeamId(Long teamId); //이번주의 데이터만 가져온다.(월~일)

    @Query("SELECT g FROM Garden g WHERE g.teamId = :teamId AND YEARWEEK(g.contributedDate, 1) >= YEARWEEK(CURRENT_DATE, 1) - :recentWeekNumber")
    List<Garden> findRecentNthWeekGardenByTeamIdOrderByContributedDateAsc(Long teamId, Integer recentWeekNumber); //최근 n주의 데이터만 가져온다.
}
