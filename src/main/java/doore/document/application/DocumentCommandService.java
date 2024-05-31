package doore.document.application;

import static doore.document.domain.DocumentGroupType.STUDY;
import static doore.document.domain.DocumentGroupType.TEAM;
import static doore.document.exception.DocumentExceptionType.INVALID_DOCUMENT_TYPE;
import static doore.document.exception.DocumentExceptionType.LINK_DOCUMENT_NEEDS_URL;
import static doore.document.exception.DocumentExceptionType.NOT_FOUND_DOCUMENT;
import static doore.document.exception.DocumentExceptionType.NO_FILE_ATTACHED;
import static doore.member.exception.MemberExceptionType.UNAUTHORIZED;
import static doore.study.exception.StudyExceptionType.NOT_FOUND_STUDY;
import static doore.team.exception.TeamExceptionType.NOT_FOUND_TEAM;

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
import doore.member.domain.repository.MemberRepository;
import doore.member.exception.MemberException;

import doore.garden.domain.Garden;
import doore.garden.domain.GardenType;
import doore.garden.domain.repository.GardenRepository;
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
    private final MemberRepository memberRepository;
    private final S3ImageFileService s3ImageFileService;
    private final S3DocumentFileService s3DocumentFileService;
    private final GardenRepository gardenRepository;

    public void createDocument(DocumentCreateRequest request, List<MultipartFile> multipartFiles,
                               DocumentGroupType groupType, Long groupId, Long memberId) {
        validateExistMember(memberId);
        validateExistGroup(groupType, groupId);
        validateDocumentType(request.type(), request.url(), multipartFiles);
        Document document = Document.from(request, groupType, groupId);

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
            List<String> filePaths = uploadFilesToS3(document.getType(), multipartFiles);
            List<File> newFiles = saveFiles(filePaths, document);
            document.updateFiles(newFiles);
        }

        createGarden(document);
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

    private List<String> uploadFilesToS3(DocumentType type, List<MultipartFile> multipartFiles) {
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

    private List<File> saveFiles(List<String> filePaths, Document document) {
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

    public void createGarden(Document document) {
        Garden garden = GardenType.getSupplierOf(document.getClass().getSimpleName()).of(document);
        gardenRepository.save(garden);
    }

    public void updateDocument(DocumentUpdateRequest request, Long documentId, Long memberId) {
        validateExistMember(memberId);
        Document document = validateExistDocument(documentId);
        if (!document.isMine(memberId)){
            throw new MemberException(UNAUTHORIZED);
        }
        document.update(request.title(), request.description(), request.accessType());
    }

    public void deleteDocument(Long documentId, Long memberId) {
        validateExistMember(memberId);
        Document document = validateExistDocument(documentId);
        if (!document.isMine(memberId)){
            throw new MemberException(UNAUTHORIZED);
        }
        deleteGarden(document);
        documentRepository.deleteById(documentId);
    }

    public void deleteGarden(Document document) {
        Long contributionId = document.getId();
        GardenType gardenType = GardenType.getGardenTypeOf(document.getClass().getSimpleName());
        gardenRepository.deleteByContributionIdAndType(contributionId, gardenType);
    }

    private Document validateExistDocument(Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(() -> new DocumentException(NOT_FOUND_DOCUMENT));
    }

    private void validateExistMember(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new MemberException(UNAUTHORIZED));
    }
}
