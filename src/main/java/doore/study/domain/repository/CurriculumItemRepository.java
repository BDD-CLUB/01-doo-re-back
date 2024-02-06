package doore.study.domain.repository;

import doore.study.domain.CurriculumItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculumItemRepository extends JpaRepository<CurriculumItem, Long> {

}
