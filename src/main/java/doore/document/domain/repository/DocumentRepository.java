package doore.document.domain.repository;

import doore.document.domain.Document;
import doore.document.domain.DocumentGroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentRepository extends JpaRepository<Document,Long> {
    @Query(value = "select COUNT(*) from Document document "
            + "where document.groupType = 'TEAM' "
            + "and document.groupId = :teamId "
            + "and document.isDeleted = false "
            + "and Date(document.createdAt) = CURRENT DATE ")
    int getTodayUploadedDocumentFromTeam(Long teamId);

    @Query(value = "select COUNT(*) from Document document "
            + "where document.groupType = 'TEAM' "
            + "and document.groupId = :teamId "
            + "and document.isDeleted = false "
            + "and Date(document.createdAt) >= ADDDATE(CURDATE(), -DAYOFWEEK(CURDATE())+2) ")
    int getWeekUploadedDocumentFromTeam(Long teamId);

    Page<Document> findAllByGroupTypeAndGroupId(DocumentGroupType groupType, Long groupId, Pageable pageable);
}
