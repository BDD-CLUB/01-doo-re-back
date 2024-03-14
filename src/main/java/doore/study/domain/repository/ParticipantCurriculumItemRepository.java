package doore.study.domain.repository;

import doore.study.domain.ParticipantCurriculumItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantCurriculumItemRepository extends JpaRepository<ParticipantCurriculumItem, Long> {
    Optional<ParticipantCurriculumItem> findByCurriculumItemIdAndParticipantId(Long curriculumId, Long participantId);

    @Query(value = "select COUNT(*) from ParticipantCurriculumItem pci "
            + "join CurriculumItem ci on pci.curriculumItem.id = ci.id "
            + "join Study s on ci.study.id = s.id "
            + "where s.teamId = :teamId "
            + "and pci.isChecked = true "
            + "and Date(pci.updatedAt) = CURRENT DATE "
            + "and pci.isDeleted = false "
            + "and ci.isDeleted = false "
            + "and s.isDeleted = false")
    int getTodayCheckedCurriculumItemFromTeam(Long teamId);

    @Query(value = "select COUNT(*) from ParticipantCurriculumItem pci "
            + "join CurriculumItem ci on pci.curriculumItem.id = ci.id "
            + "join Study s on ci.study.id = s.id "
            + "where s.teamId = :teamId "
            + "and pci.isChecked = true "
            + "and Date(pci.updatedAt) >= ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2) "
            + "and pci.isDeleted = false "
            + "and ci.isDeleted = false "
            + "and s.isDeleted = false")
    int getWeekCheckedCurriculumItemFromTeam(Long teamId);
}
