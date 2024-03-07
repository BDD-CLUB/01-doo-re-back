package doore.document.domain.repository;

import doore.document.domain.StudyDocument;
import doore.document.domain.DocumentGroupType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<StudyDocument,Long> {
    Page<StudyDocument> findAllByGroupTypeAndGroupId(DocumentGroupType groupType, Long groupId, Pageable pageable);
}
