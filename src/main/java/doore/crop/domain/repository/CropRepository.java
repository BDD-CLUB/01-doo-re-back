package doore.crop.domain.repository;

import doore.crop.domain.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropRepository extends JpaRepository<Crop, Long> {

}
