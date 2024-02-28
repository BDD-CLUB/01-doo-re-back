package doore.document.domain.repository;

import doore.document.domain.StudyDocument;
import doore.document.domain.DocumentGroupType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<StudyDocument,Long> {
    List<StudyDocument> findAllByGroupTypeAndGroupId(DocumentGroupType groupType, Long groupId);
}
