package doore.document;

import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.DocumentType;
import doore.document.domain.File;
import doore.document.domain.StudyDocument;
import doore.document.domain.repository.DocumentRepository;
import doore.document.domain.repository.FileRepository;
import doore.study.StudyFixture;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentFixture {

    private static DocumentRepository documentRepository;
    private static FileRepository fileRepository;

    private String name = "학습자료 파일";
    private String description = "";
    private DocumentGroupType groupType = DocumentGroupType.teams;
    private Long groupId = 1L;
    private DocumentAccessType accessType = DocumentAccessType.all;
    private DocumentType type = DocumentType.url;
    private Long uploaderId = 1L;

    @Autowired
    public DocumentFixture(DocumentRepository documentRepository, FileRepository fileRepository) {
        DocumentFixture.documentRepository = documentRepository;
        DocumentFixture.fileRepository = fileRepository;
    }

    public DocumentFixture() {

    }

    public DocumentFixture name(String name) {
        this.name = name;
        return this;
    }

    public DocumentFixture description(String description) {
        this.description = description;
        return this;
    }

    public DocumentFixture groupType(DocumentGroupType groupType) {
        this.groupType = groupType;
        return this;
    }

    public DocumentFixture groupId(Long groupId) {
        this.groupId = groupId;
        return this;
    }

    public DocumentFixture accessType(DocumentAccessType accessType) {
        this.accessType = accessType;
        return this;
    }

    public DocumentFixture uploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
        return this;
    }

    public DocumentFixture type(DocumentType type) {
        this.type = type;
        return this;
    }

    public StudyDocument buildStudyDocument() {
        final StudyDocument studyDocument = StudyDocument.builder()
                .name(this.name)
                .description(this.description)
                .groupType(this.groupType)
                .groupId(this.groupId)
                .accessType(this.accessType)
                .uploaderId(this.uploaderId)
                .type(this.type)
                .build();
        return documentRepository.save(studyDocument);
    }

    public StudyDocument buildLinkDocument(List<String> urls) {
        type(DocumentType.url);
        StudyDocument studyDocument = buildStudyDocument();
        List<File> files = new ArrayList<>();
        for (String url : urls) {
            File file = File.builder()
                    .url(url)
                    .document(studyDocument)
                    .build();
            files.add(file);
        }

        fileRepository.saveAll(files);
        studyDocument.updateFiles(files);
        return studyDocument;
    }
}
