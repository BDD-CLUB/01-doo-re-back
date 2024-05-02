package doore.garden.domain.repository;

import doore.garden.domain.Garden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GardenRepository extends JpaRepository<Garden,Long> {
}
