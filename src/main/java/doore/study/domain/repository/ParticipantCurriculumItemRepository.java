package doore.study.domain.repository;

import doore.study.domain.ParticipantCurriculumItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantCurriculumItemRepository extends JpaRepository<ParticipantCurriculumItem, Long> {
    Optional<ParticipantCurriculumItem> findByCurriculumItemIdAndParticipantId(Long curriculumId, Long participantId);
}
