package doore.study.domain;

import static doore.study.domain.StudyStatus.ENDED;

import doore.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private Long cropId;

    @OneToMany(mappedBy = "study")
    private List<CurriculumItem> curriculumItems;


    public void createCurriculumItems(List<CurriculumItem> newCurriculumItems) {
        if (curriculumItems == null) {
            return;
        }
        newCurriculumItems.forEach(newCurriculumItem -> {
            newCurriculumItem.setStudy(this);
        });
        curriculumItems.addAll(newCurriculumItems);
    }

    public void update(String name, String description, LocalDate startDate, LocalDate endDate, StudyStatus status) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public boolean isEnded() {
        return this.status == ENDED;
    }

    @Builder
    public Study(String name, String description, LocalDate startDate, LocalDate endDate, StudyStatus status,
                 Boolean isDeleted, Long teamId, Long cropId, List<CurriculumItem> curriculumItems) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.isDeleted = isDeleted;
        this.teamId = teamId;
        this.cropId = cropId;
        this.curriculumItems = (curriculumItems != null) ? curriculumItems : new ArrayList<>();
        ;
    }

    public void terminate() {
        this.status = ENDED;
    }
}
