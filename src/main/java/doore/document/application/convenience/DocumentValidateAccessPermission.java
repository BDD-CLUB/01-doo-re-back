package doore.document.application.convenience;

import static doore.document.exception.DocumentExceptionType.LINK_DOCUMENT_NEEDS_URL;
import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.document.exception.DocumentExceptionType.NO_FILE_ATTACHED;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.document.domain.Document;
import doore.document.domain.DocumentType;
import doore.document.domain.repository.DocumentRepository;
import doore.document.exception.DocumentException;
import doore.member.exception.MemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentValidateAccessPermission {

    private final DocumentRepository documentRepository;

    public Document getValidateExistDocument(final Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
    }

    public void validateDocumentType(final DocumentType type, final String url,
                                      final List<MultipartFile> multipartFiles) {
        if (type.equals(DocumentType.URL) && url == null) {
            throw new DocumentException(LINK_DOCUMENT_NEEDS_URL);
        }
        if (!type.equals(DocumentType.URL) && (multipartFiles == null || multipartFiles.isEmpty())) {
            throw new DocumentException(NO_FILE_ATTACHED);
        }
    }

    public void validateMyDocument(final Document document, final Long memberId) {
        if (!document.isMine(memberId)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }

    public boolean isMyDocument(final Document document, final Long memberId) {
        return document.isMine(memberId);
    }
}
