package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.member.domain.StudyRoleType.ROLE_스터디원;
import static doore.member.domain.StudyRoleType.ROLE_스터디장;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static doore.member.exception.MemberExceptionType.NOT_FOUND_MEMBER_ROLE_IN_STUDY;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.document.application.dto.response.DocumentResponse;
import doore.document.application.dto.response.FileResponse;
import doore.document.domain.Document;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.repository.DocumentRepository;
import doore.document.exception.DocumentException;
import doore.member.domain.StudyRole;
import doore.member.domain.repository.MemberRepository;
import doore.member.domain.repository.StudyRoleRepository;
import doore.member.exception.MemberException;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
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

    public List<DocumentResponse> getAllDocument(
            final DocumentGroupType groupType, final Long groupId, final Pageable pageable) {

        return documentRepository.findAllByGroupTypeAndGroupId(groupType, groupId, pageable)
                .map(this::toDocumentResponse).getContent();
    }

    public DocumentResponse getDocument(final Long documentId, final Long memberId) {
        final Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
        final DocumentGroupType documentGroupType = document.getGroupType();
        final Study study = studyRepository.findByDocumentId(documentId);
        if (documentGroupType == STUDY) {
            validateMemberRoleForStudy(study.getId(), memberId);
        }
        return toDocumentResponse(document);
    }

    private DocumentResponse toDocumentResponse(final Document document) {
        final List<FileResponse> fileResponses = document.getFiles().stream()
                .map(file -> new FileResponse(file.getId(), file.getUrl()))
                .toList();

        final String uploaderName = memberRepository.findById(document.getUploaderId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER))
                .getName();

        return DocumentResponse.of(document, fileResponses, uploaderName);
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
