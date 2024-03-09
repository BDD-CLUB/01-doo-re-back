package doore.document.application;

import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import doore.document.application.dto.response.DocumentCondensedResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.application.dto.response.FileResponse;
import doore.document.domain.Document;
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

    private DocumentCondensedResponse toDocumentCondensedResponse(Document document) {
        Long uploaderId = memberRepository.findById(document.getUploaderId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER)).getId();
        return new DocumentCondensedResponse(document.getId(), document.getName(), document.getDescription(),
                document.getCreatedAt().toLocalDate(), uploaderId);
    }

    public DocumentDetailResponse getDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
        return toDocumentDetailResponse(document);
    }

    private DocumentDetailResponse toDocumentDetailResponse(Document document) {
        List<FileResponse> fileResponses = new ArrayList<>();
        for (File file : document.getFiles()) {
            FileResponse fileResponse = new FileResponse(file.getId(), file.getUrl());
            fileResponses.add(fileResponse);
        }
        String uploaderName = memberRepository.findById(document.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER))
                .getName();

        return DocumentDetailResponse
                .builder()
                .id(document.getId())
                .title(document.getName())
                .description(document.getDescription())
                .accessType(document.getAccessType())
                .type(document.getType())
                .files(fileResponses)
                .date(document.getCreatedAt().toLocalDate())
                .uploader(uploaderName)
                .build();
    }
}
