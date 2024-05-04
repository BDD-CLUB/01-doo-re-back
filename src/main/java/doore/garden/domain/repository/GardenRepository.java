package doore.garden.domain.repository;

import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GardenRepository extends JpaRepository<Garden,Long> {
    void deleteByContributionIdAndType(Long contributionId, GardenType gardenType);
}
