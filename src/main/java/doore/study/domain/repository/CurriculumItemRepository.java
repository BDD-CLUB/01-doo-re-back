package doore.study.domain.repository;

import doore.study.domain.CurriculumItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CurriculumItemRepository extends JpaRepository<CurriculumItem, Long> {
    List<CurriculumItem> findAllByOrderByItemOrderAsc();

    List<CurriculumItem> findAllByStudyId(Long studyId);

    void deleteAllByStudyId(Long studyId);

    @Query("SELECT ci.id FROM CurriculumItem ci WHERE ci.study.id = :studyId")
    List<Long> findIdsByStudyId(Long studyId);

    Long countByStudyId(Long studyId);

    List<CurriculumItem> findAllByStudyIdOrderByItemOrderAsc(Long studyId);
}
