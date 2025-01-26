package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.domain.DocumentGroupType.TEAM;
import static doore.document.exception.DocumentExceptionType.INVALID_DOCUMENT_TYPE;

import doore.document.application.convenience.DocumentValidateAccessPermission;
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
import doore.member.application.convenience.MemberValidateAccessPermission;
import doore.study.application.convenience.StudyValidateAccessPermission;
import doore.team.application.convenience.TeamValidateAccessPermission;
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
    private final DocumentRepository documentRepository;
    private final FileRepository fileRepository;

    private final S3ImageFileService s3ImageFileService;
    private final S3DocumentFileService s3DocumentFileService;
    private final GardenConvenience gardenCommandService;

    private final TeamValidateAccessPermission teamValidateAccessPermission;
    private final StudyValidateAccessPermission studyValidateAccessPermission;
    private final MemberValidateAccessPermission memberValidateAccessPermission;
    private final DocumentValidateAccessPermission documentValidateAccessPermission;

    public void createDocument(final DocumentCreateRequest request, final List<MultipartFile> multipartFiles,
                               final DocumentGroupType groupType, final Long groupId, final Long memberId) {
        memberValidateAccessPermission.validateExistMember(memberId);
        validateExistGroup(groupType, groupId);
        documentValidateAccessPermission.validateDocumentType(request.type(), request.url(), multipartFiles);
        final Document document = Document.from(request, groupType, groupId);

        documentRepository.save(document);

        if (document.getType().equals(DocumentType.URL)) {
            final File newFile = createFile(request.url(), "", document);
            document.updateFiles(List.of(newFile));
        }
        if (!document.getType().equals(DocumentType.URL)) {
            final List<File> newFiles = new ArrayList<>();
            for (MultipartFile file : multipartFiles) {
                final String filename = file.getOriginalFilename();
                final String filePath = uploadFileToS3(document.getType(), file);
                final File newFile = createFile(filePath, filename, document);
                newFiles.add(newFile);
            }
            document.updateFiles(newFiles);
        }
        gardenCommandService.createDocumentGarden(document);
    }

    public void updateDocument(final DocumentUpdateRequest request, final Long documentId, final Long memberId) {
        memberValidateAccessPermission.validateExistMember(memberId);
        final Document document = documentValidateAccessPermission.getValidateExistDocument(documentId);
        documentValidateAccessPermission.validateMyDocument(document, memberId);
        document.update(request.title(), request.description(), request.accessType());
    }

    public void deleteDocument(final Long documentId, final Long memberId) {
        memberValidateAccessPermission.validateExistMember(memberId);
        final Document document = documentValidateAccessPermission.getValidateExistDocument(documentId);
        documentValidateAccessPermission.validateMyDocument(document, memberId);
        gardenCommandService.deleteDocumentGarden(document);
        documentRepository.deleteById(documentId);
    }

    private void validateExistGroup(final DocumentGroupType groupType, final Long groupId) {
        if (groupType.equals(TEAM)) {
            teamValidateAccessPermission.validateExistTeam(groupId);
        }
        if (groupType.equals(STUDY)) {
            studyValidateAccessPermission.validateExistStudy(groupId);
        }
    }

    //todo: 파일 관련 코드 정리 필요
    private String uploadFileToS3(final DocumentType type, final MultipartFile file) {
        if (type.equals(DocumentType.IMAGE)) {
            return s3ImageFileService.upload(file);
        }
        if (type.equals(DocumentType.DOCUMENT)) {
            return s3DocumentFileService.upload(file);
        }
        throw new DocumentException(INVALID_DOCUMENT_TYPE);
    }

    private File createFile(final String filePath, final String fileName, final Document document) {
        return fileRepository.save(File.builder()
                .document(document)
                .name(fileName)
                .url(filePath)
                .build());
    }
}
