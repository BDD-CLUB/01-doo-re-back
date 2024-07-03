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
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentQueryService {

    private final DocumentRepository documentRepository;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyRoleRepository studyRoleRepository;

    public List<DocumentCondensedResponse> getAllDocument(
            final DocumentGroupType groupType, final Long groupId, final Pageable pageable) {

        return documentRepository.findAllByGroupTypeAndGroupId(groupType, groupId, pageable)
                .map(this::toDocumentCondensedResponse).getContent();
    }

    private DocumentCondensedResponse toDocumentCondensedResponse(final Document document) {
        final Long uploaderId = memberRepository.findById(document.getUploaderId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER)).getId();
        return new DocumentCondensedResponse(document.getId(), document.getName(), document.getDescription(),
                document.getCreatedAt().toLocalDate(), uploaderId);
    }

    public DocumentDetailResponse getDocument(final Long documentId, final Long memberId) {
        final Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
        final DocumentGroupType documentGroupType = document.getGroupType();
        final Study study = studyRepository.findByDocumentId(documentId);
        if (documentGroupType == STUDY) {
            validateMemberRoleForStudy(study.getId(), memberId);
        }
        return toDocumentDetailResponse(document);
    }

    private DocumentDetailResponse toDocumentDetailResponse(final Document document) {
        final List<FileResponse> fileResponses = new ArrayList<>();
        for (final File file : document.getFiles()) {
            final FileResponse fileResponse = new FileResponse(file.getId(), file.getUrl());
            fileResponses.add(fileResponse);
        }
        final String uploaderName = memberRepository.findById(document.getId())
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

    private void validateExistMember(final Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(UNAUTHORIZED));
    }

    private void validateMemberRoleForStudy(final Long studyId, final Long memberId) {
        final StudyRole studyRole = studyRoleRepository.findStudyRoleByStudyIdAndMemberId(studyId, memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ROLE_IN_STUDY));
        if (!(studyRole.getStudyRoleType() == ROLE_스터디장 || studyRole.getStudyRoleType() == ROLE_스터디원)) {
            throw new MemberException(UNAUTHORIZED);
        }
    }
}
