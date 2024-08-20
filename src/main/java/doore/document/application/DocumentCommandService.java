package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.domain.DocumentGroupType.TEAM;
import static doore.document.exception.DocumentExceptionType.INVALID_DOCUMENT_TYPE;
import static doore.document.exception.DocumentExceptionType.LINK_DOCUMENT_NEEDS_URL;
import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.document.exception.DocumentExceptionType.NO_FILE_ATTACHED;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;

import doore.document.application.dto.request.DocumentCreateRequest;
import doore.document.application.dto.request.DocumentUpdateRequest;
import doore.document.domain.Document;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.DocumentType;
import doore.document.domain.File;
import doore.document.domain.repository.DocumentRepository;
import doore.document.domain.repository.FileRepository;
import doore.document.exception.DocumentException;
import doore.file.application.S3DocumentFileService;
import doore.file.application.S3ImageFileService;
import doore.garden.application.convenience.GardenConvenience;
import doore.member.application.convenience.MemberAuthorization;
import doore.member.exception.MemberException;
import doore.study.application.convenience.StudyAuthorization;
import doore.team.application.convenience.TeamAuthorization;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class DocumentCommandService {
    private final TeamAuthorization teamAuthorization;
    private final StudyAuthorization studyAuthorization;
    private final MemberAuthorization memberAuthorization;
    private final S3ImageFileService s3ImageFileService;
    private final S3DocumentFileService s3DocumentFileService;
    private final GardenConvenience gardenCommandService;
    private final DocumentRepository documentRepository;
    private final FileRepository fileRepository;

    public void createDocument(final DocumentCreateRequest request, final List<MultipartFile> multipartFiles,
                               final DocumentGroupType groupType, final Long groupId, final Long memberId) {
        memberAuthorization.validateExistMember(memberId);
        validateExistGroup(groupType, groupId);
        validateDocumentType(request.type(), request.url(), multipartFiles);
        final Document document = Document.from(request, groupType, groupId);

        documentRepository.save(document);

        if (document.getType().equals(DocumentType.URL)) {
            final File newFile = saveFile(request.url(), "", document);
            document.updateFiles(List.of(newFile));
        }
        if (!document.getType().equals(DocumentType.URL)) {
            final List<File> newFiles = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                final String filename = file.getName();
                final String filePath = uploadFileToS3(document.getType(), file);
                final File newFile = saveFile(filePath, filename, document);
                newFiles.add(newFile);
            }
            document.updateFiles(newFiles);
        }
        gardenCommandService.createDocumentGarden(document);
    }

    private void validateExistGroup(final DocumentGroupType groupType, final Long groupId) {
        if (groupType.equals(TEAM)) {
            teamAuthorization.validateExistTeam(groupId);
        }
        if (groupType.equals(STUDY)) {
            studyAuthorization.validateExistStudy(groupId);
        }
    }

    private void validateDocumentType(final DocumentType type, final String url,
                                      final List<MultipartFile> multipartFiles) {
        if (type.equals(DocumentType.URL) && url == null) {
            throw new DocumentException(LINK_DOCUMENT_NEEDS_URL);
        }
        if (!type.equals(DocumentType.URL) && (multipartFiles == null || multipartFiles.isEmpty())) {
            throw new DocumentException(NO_FILE_ATTACHED);
        }
    }

    private String uploadFileToS3(final DocumentType type, final MultipartFile file) {
        if (type.equals(DocumentType.IMAGE)) {
            return s3ImageFileService.upload(file);
        }
        if (type.equals(DocumentType.DOCUMENT)) {
            return s3DocumentFileService.upload(file);
        }
        throw new DocumentException(INVALID_DOCUMENT_TYPE);
    }

    private File saveFile(final String filePath, final String fileName, final Document document) {
        final File newfile = File.builder()
                .document(document)
                .name(fileName)
                .url(filePath)
                .build();
        return fileRepository.save(newfile);
    }

    public void updateDocument(final DocumentUpdateRequest request, final Long documentId, final Long memberId) {
        memberAuthorization.validateExistMember(memberId);
        final Document document = validateExistDocument(documentId);
        if (!document.isMine(memberId)) {
            throw new MemberException(UNAUTHORIZED);
        }
        document.update(request.title(), request.description(), request.accessType());
    }

    public void deleteDocument(final Long documentId, final Long memberId) {
        memberAuthorization.validateExistMember(memberId);
        final Document document = validateExistDocument(documentId);
        if (!document.isMine(memberId)) {
            throw new MemberException(UNAUTHORIZED);
        }
        gardenCommandService.deleteDocumentGarden(document);
        documentRepository.deleteById(documentId);
    }

    private Document validateExistDocument(final Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
    }
}
