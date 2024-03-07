package doore.document.application;

import static doore.document.domain.DocumentGroupType.*;
import static doore.document.exception.DocumentExceptionType.*;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

import doore.document.application.dto.request.DocumentCreateRequest;
import doore.document.application.dto.request.DocumentUpdateRequest;
import doore.document.domain.StudyDocument;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.DocumentType;
import doore.document.domain.File;
import doore.document.domain.repository.DocumentRepository;
import doore.document.domain.repository.FileRepository;
import doore.document.exception.DocumentException;
import doore.file.application.S3DocumentFileService;
import doore.file.application.S3ImageFileService;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.StudyException;
import doore.team.domain.TeamRepository;
import doore.team.exception.TeamException;
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
    private final TeamRepository teamRepository;
    private final StudyRepository studyRepository;
    private final FileRepository fileRepository;
    private final S3ImageFileService s3ImageFileService;
    private final S3DocumentFileService s3DocumentFileService;

    public void createDocument(DocumentCreateRequest request, List<MultipartFile> multipartFiles,
                               DocumentGroupType groupType, Long groupId) {
        validateExistGroup(groupType, groupId);
        validateDocumentType(request.type(), request.url(), multipartFiles);
        StudyDocument document = toDocument(request, groupType, groupId);

        documentRepository.save(document);

        if (document.getType().equals(DocumentType.URL)) {
            File newFile = File.builder()
                    .url(request.url())
                    .document(document)
                    .build();
            fileRepository.save(newFile);
            document.updateFiles(List.of(newFile));
        }
        if (!document.getType().equals(DocumentType.URL)) {
            List<String> filePaths = uploadFileToS3(document.getType(), multipartFiles);
            List<File> newFiles = saveFile(filePaths, document);
            document.updateFiles(newFiles);
        }
    }

    private void validateExistGroup(DocumentGroupType groupType, Long groupId) {
        if (groupType.equals(TEAM)) {
            teamRepository.findById(groupId).orElseThrow(() -> new TeamException(NOT_FOUND_TEAM));
        }
        if (groupType.equals(STUDY)) {
            studyRepository.findById(groupId).orElseThrow(() -> new StudyException(NOT_FOUND_STUDY));
        }
    }

    private void validateDocumentType(DocumentType type, String url, List<MultipartFile> multipartFiles) {
        if (type.equals(DocumentType.URL) && url == null) {
            throw new DocumentException(LINK_DOCUMENT_NEEDS_URL);
        }
        if (!type.equals(DocumentType.URL) && (multipartFiles == null || multipartFiles.isEmpty())) {
            throw new DocumentException(NO_FILE_ATTACHED);
        }
    }

    private StudyDocument toDocument(DocumentCreateRequest request, DocumentGroupType groupType, Long groupId) {
        return StudyDocument.builder()
                .name(request.title())
                .description(request.description())
                .groupType(groupType)
                .groupId(groupId)
                .accessType(request.accessType())
                .type(request.type())
                .uploaderId(request.uploaderId())
                .build();
    }

    private List<String> uploadFileToS3(DocumentType type, List<MultipartFile> multipartFiles) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String url = "";
            if (type.equals(DocumentType.IMAGE)) {
                url = s3ImageFileService.upload(multipartFile);
                urls.add(url);
                continue;
            }
            if (type.equals(DocumentType.FILE)) {
                url = s3DocumentFileService.upload(multipartFile);
                urls.add(url);
                continue;
            }
            throw new DocumentException(INVALID_DOCUMENT_TYPE);
        }
        return urls;
    }

    private List<File> saveFile(List<String> filePaths, StudyDocument document) {
        List<File> files = new ArrayList<>();
        for (String filePath : filePaths) {
            File newfile = File.builder()
                    .document(document)
                    .url(filePath)
                    .build();

            files.add(newfile);
            fileRepository.save(newfile);
        }
        return files;
    }

    public void updateDocument(DocumentUpdateRequest request, Long documentId) {
        StudyDocument document = validateExistDocument(documentId);
        document.update(request.title(), request.description(), request.accessType());
    }

    public void deleteDocument(Long documentId) {
        validateExistDocument(documentId);
        documentRepository.deleteById(documentId);
    }

    private StudyDocument validateExistDocument(Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
    }
}
