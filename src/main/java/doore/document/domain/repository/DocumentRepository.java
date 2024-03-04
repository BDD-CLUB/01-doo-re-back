package doore.document.domain.repository;

import doore.document.domain.Document;
import doore.document.domain.DocumentGroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
    Page<Document> findAllByGroupTypeAndGroupId(DocumentGroupType groupType, Long groupId, Pageable pageable);
}
