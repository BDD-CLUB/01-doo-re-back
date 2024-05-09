package doore.garden.domain.repository;

import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GardenRepository extends JpaRepository<Garden,Long> {
    void deleteByContributionIdAndType(Long contributionId, GardenType gardenType);
    @Query("SELECT g FROM Garden g WHERE g.teamId = :teamId AND YEAR(g.contributedDate) = YEAR(CURRENT_DATE) ORDER BY g.contributedDate ASC")
    List<Garden> findAllOfThisYearByTeamIdOrderByContributedDateAsc(Long teamId); //올해의 데이터만 가져온다.
}
