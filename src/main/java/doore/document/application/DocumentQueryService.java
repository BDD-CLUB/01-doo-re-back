package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;

import doore.document.application.dto.response.DocumentResponse;
import doore.document.application.dto.response.FileResponse;
import doore.document.domain.Document;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.repository.DocumentRepository;
import doore.document.exception.DocumentException;
import doore.member.application.convenience.MemberConvenience;
import doore.member.application.convenience.StudyRoleValidateAccessPermission;
import doore.study.application.convenience.StudyConvenience;
import doore.study.domain.Study;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentQueryService {

    private final DocumentRepository documentRepository;
    private final MemberConvenience memberConvenience;
    private final StudyConvenience studyConvenience;
    private final StudyRoleValidateAccessPermission studyRoleValidateAccessPermission;

    public Page<DocumentResponse> getAllDocument(
            final DocumentGroupType groupType, final Long groupId, final Pageable pageable) {

        return documentRepository.findAllByGroupTypeAndGroupId(groupType, groupId, pageable)
                .map(this::toDocumentResponse);
    }

    public DocumentResponse getDocument(final Long documentId, final Long memberId) {
        final Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
        final DocumentGroupType documentGroupType = document.getGroupType();
        final Study study = studyConvenience.findByDocumentId(documentId);
        if (documentGroupType == STUDY) {
            studyRoleValidateAccessPermission.validateExistParticipant(study.getId(), memberId);
        }
        return toDocumentResponse(document);
    }

    private DocumentResponse toDocumentResponse(final Document document) {
        final List<FileResponse> fileResponses = document.getFiles().stream()
                .map(file -> new FileResponse(file.getId(), file.getName(), file.getUrl()))
                .toList();

        final String uploaderName = memberConvenience.findById(document.getUploaderId()).getName();

        return DocumentResponse.of(document, fileResponses, uploaderName);
    }
}
