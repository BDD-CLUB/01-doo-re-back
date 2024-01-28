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

@Entity
@Getter
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
    private final List<CurriculumItem> curriculumItems = new ArrayList<>();


    public void createCurriculumItems(List<CurriculumItem> newCurriculumItems) {
        newCurriculumItems.forEach(newCurriculumItem -> newCurriculumItem.saveStudy(this));
        curriculumItems.addAll(newCurriculumItems);
    }

    public void update(String name, String description, LocalDate startDate, LocalDate endDate, StudyStatus status) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    @Builder
    private Study(String name, String description, LocalDate startDate, LocalDate endDate, StudyStatus status,
                 Boolean isDeleted, Long teamId, Long cropId, List<CurriculumItem> curriculumItems) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.isDeleted = isDeleted;
        this.teamId = teamId;
        this.cropId = cropId;
        if (curriculumItems != null) {
            this.curriculumItems.addAll(curriculumItems);
        }
    }

    public void terminate() {
        this.status = ENDED;
    }

    public void changeStatus(StudyStatus status) {
        this.status = status;
    }
}
