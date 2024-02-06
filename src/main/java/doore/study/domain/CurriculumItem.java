package doore.study.domain;

import static jakarta.persistence.CascadeType.REMOVE;

import doore.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurriculumItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer itemOrder;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @OneToMany(mappedBy = "curriculumItem", cascade = REMOVE)
    private final List<ParticipantCurriculumItem> participantCurriculumItems = new ArrayList<>();

    @Builder
    private CurriculumItem(Long id, String name, Integer itemOrder, Study study) {
        this.id = id;
        this.name = name;
        this.itemOrder = itemOrder;
        this.isDeleted = false;
        this.study = study;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public void saveStudy(Study study) {
        this.study = study;
    }
}
