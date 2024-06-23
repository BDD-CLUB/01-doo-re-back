package doore.document;

import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentGroupType;
import doore.document.domain.DocumentType;
import doore.document.domain.File;
import doore.document.domain.Document;
import doore.document.domain.repository.DocumentRepository;
import doore.document.domain.repository.FileRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentFixture {

    private static DocumentRepository documentRepository;
    private static FileRepository fileRepository;

    private String name = "학습자료 파일";
    private String description = "";
    private DocumentGroupType groupType = DocumentGroupType.TEAM;
    private Long groupId = 1L;
    private DocumentAccessType accessType = DocumentAccessType.ALL;
    private DocumentType type = DocumentType.URL;
    private Long uploaderId = 1L;

    @Autowired
    public DocumentFixture(final DocumentRepository documentRepository, final FileRepository fileRepository) {
        DocumentFixture.documentRepository = documentRepository;
        DocumentFixture.fileRepository = fileRepository;
    }

    public DocumentFixture() {

    }

    public DocumentFixture name(final String name) {
        this.name = name;
        return this;
    }

    public DocumentFixture description(final String description) {
        this.description = description;
        return this;
    }

    public DocumentFixture groupType(final DocumentGroupType groupType) {
        this.groupType = groupType;
        return this;
    }

    public DocumentFixture groupId(final Long groupId) {
        this.groupId = groupId;
        return this;
    }

    public DocumentFixture accessType(final DocumentAccessType accessType) {
        this.accessType = accessType;
        return this;
    }

    public DocumentFixture uploaderId(final Long uploaderId) {
        this.uploaderId = uploaderId;
        return this;
    }

    public DocumentFixture type(final DocumentType type) {
        this.type = type;
        return this;
    }

    public Document buildDocument() {
        final Document document = Document.builder()
                .name(this.name)
                .description(this.description)
                .groupType(this.groupType)
                .groupId(this.groupId)
                .accessType(this.accessType)
                .uploaderId(this.uploaderId)
                .type(this.type)
                .build();
        return documentRepository.save(document);
    }

    public Document buildLinkDocument(final List<String> urls) {
        type(DocumentType.URL);
        final Document document = buildDocument();
        final List<File> files = new ArrayList<>();
        for (final String url : urls) {
            final File file = File.builder()
                    .url(url)
                    .document(document)
                    .build();
            files.add(file);
        }

        fileRepository.saveAll(files);
        document.updateFiles(files);
        return document;
    }
}
