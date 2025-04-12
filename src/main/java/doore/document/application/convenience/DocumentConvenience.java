package doore.document.application.convenience;

import doore.document.domain.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentConvenience {

    private final DocumentRepository documentRepository;

    public Long countByGroupId(final Long groupId) {
        return documentRepository.countByGroupId(groupId);
    }
}
