package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.document.application.dto.response.DocumentCondensedResponse;
import doore.document.application.dto.response.DocumentDetailResponse;
import doore.document.application.dto.response.FileResponse;
import doore.document.domain.Document;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.File;
import doore.document.domain.repository.DocumentRepository;
import doore.document.exception.DocumentException;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import java.util.ArrayList;
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
    private final StudyRoleRepository studyRoleRepository;

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

    public DocumentDetailResponse getDocument(Long documentId, Long memberId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
        DocumentGroupType documentGroupType = document.getGroupType();
        if (documentGroupType == STUDY) {
            validateMemberRoleForStudy(memberId);
        }
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

    private void validateExistMember(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(UNAUTHORIZED));
    }

    private void validateMemberRoleForStudy(Long memberId) {
        StudyRole studyRole = studyRoleRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!(studyRole.getStudyRoleType() == ROLE_스터디장 || studyRole.getStudyRoleType() == ROLE_스터디원)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
