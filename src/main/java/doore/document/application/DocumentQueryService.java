package doore.document.application;

import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import doore.document.application.dto.response.DocumentCondensedResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.application.dto.response.FileResponse;
import doore.document.domain.StudyDocument;
import doore.document.exception.DocumentException;
import java.util.ArrayList;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.File;
import doore.document.domain.repository.DocumentRepository;
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;
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
    private final MemberRepository memberRepository;

    public Page<DocumentCondensedResponse> getAllDocument(
            DocumentGroupType groupType, Long groupId, Pageable pageable) {
        return documentRepository.findAllByGroupTypeAndGroupId(groupType, groupId, pageable)
                .map(this::toDocumentCondensedResponse);
    }

    private DocumentCondensedResponse toDocumentCondensedResponse(StudyDocument document) {
        Long uploaderId = memberRepository.findById(document.getUploaderId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER)).getId();
        return new DocumentCondensedResponse(document.getId(), document.getName(), document.getDescription(),
                document.getCreatedAt().toLocalDate(), uploaderId);
    }

    public DocumentDetailResponse getDocument(Long documentId) {
        StudyDocument studyDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
        return toDocumentDetailResponse(studyDocument);
    }

    private DocumentDetailResponse toDocumentDetailResponse(StudyDocument document) {
        List<FileResponse> fileResponses = new ArrayList<>();
        for (File file : document.getFiles()) {
            FileResponse fileResponse = new FileResponse(file.getId(), file.getUrl());
            fileResponses.add(fileResponse);
        }

        return new DocumentDetailResponse(document.getId(), document.getName(), document.getDescription(),
                document.getAccessType(), document.getType(), fileResponses,
                document.getCreatedAt().toLocalDate(), "팜");
    }
}
