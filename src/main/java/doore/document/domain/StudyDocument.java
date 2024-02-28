package doore.document.domain;

import static jakarta.persistence.CascadeType.REMOVE;

import doore.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Table(name = "document")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE document SET is_deleted = true where id = ?")
public class StudyDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentGroupType groupType;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentAccessType accessType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @OneToMany(mappedBy = "document", cascade = REMOVE)
    @Column(nullable = false)
    private final List<File> files = new ArrayList<>();

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Builder
    private StudyDocument(String name, String description, DocumentGroupType groupType, Long groupId,
                          DocumentAccessType accessType, DocumentType type, Long uploaderId) {
        this.name = name;
        this.description = description;
        this.groupType = groupType;
        this.groupId = groupId;
        this.accessType = accessType;
        this.type = type;
        this.uploaderId = uploaderId;
        this.isDeleted = false;
    }

    public void update(String name, String description, DocumentAccessType accessType) {
        this.name = name;
        this.description = description;
        this.accessType = accessType;
    }

    public void updateFiles(List<File> files) {
        this.files.clear();
        this.files.addAll(files);
    }
}
