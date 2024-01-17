package doore.study.domain;

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
import java.util.List;
import lombok.AccessLevel;
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
    private LocalDate endTime;

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

}
