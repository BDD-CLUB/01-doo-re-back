package doore.study.domain.repository;

import doore.study.domain.ParticipantCurriculumItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantCurriculumItemRepository extends JpaRepository<ParticipantCurriculumItem, Long> {
    Optional<ParticipantCurriculumItem> findByCurriculumItemIdAndParticipantId(Long curriculumId, Long participantId);

    @Query(value = "select COUNT(*) from ParticipantCurriculumItem pci "
            + "join CurriculumItem ci on pci.curriculumItem.id = ci.id "
            + "where ci.study.id = :studyId "
            + "and pci.isChecked = true "
            + "and pci.isDeleted = false "
            + "and ci.isDeleted = false ")
    int getTotalCheckedCurriculumNumberFromStudy(Long studyId);

    @Query(value = "select COUNT(*) from ParticipantCurriculumItem pci "
            + "join CurriculumItem ci on pci.curriculumItem.id = ci.id "
            + "where ci.study.id = :studyId "
            + "and pci.participantId = :participantId")
    boolean countByParticipantIdAndStudyId(Long studyId, Long participantId);
}
