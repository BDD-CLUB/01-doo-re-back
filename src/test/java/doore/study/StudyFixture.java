package doore.study;

import static doore.study.domain.StudyStatus.UPCOMING;

import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyFixture {
    private static StudyRepository studyRepository;

    private String name = "알고리즘 스터디";
    private String description = "스터디 설명";
    private LocalDate startDate = LocalDate.parse("2023-01-01");
    private LocalDate endDate = LocalDate.parse("2023-01-02");
    private Long teamId =1L;
    private Long cropId = 1L;
    private Boolean isDeleted = false;
    private List<CurriculumItem> curriculumItems = new ArrayList<>();
    private StudyStatus status = UPCOMING;


    @Autowired
    public StudyFixture(StudyRepository studyRepository) {
        StudyFixture.studyRepository = studyRepository;
    }

    private StudyFixture() {
    }

    public static StudyFixture builder() {
        return new StudyFixture();
    }

    public StudyFixture name(String name) {
        this.name = name;
        return this;
    }

    public StudyFixture description(String description) {
        this.description = description;
        return this;
    }

    public StudyFixture startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public StudyFixture endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public StudyFixture teamId(Long teamId) {
        this.teamId = teamId;
        return this;
    }

    public StudyFixture cropId(Long cropId) {
        this.cropId = cropId;
        return this;
    }

    public StudyFixture deleted(Boolean deleted) {
        isDeleted = deleted;
        return this;
    }

    public StudyFixture curriculumItems(List<CurriculumItem> curriculumItems) {
        this.curriculumItems = curriculumItems;
        return this;
    }

    public StudyFixture status(StudyStatus status) {
        this.status = status;
        return this;
    }

    public Study studyBuild() {
        Study study = Study.builder()
                .name(this.name)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .teamId(this.teamId)
                .status(this.status)
                .isDeleted(this.isDeleted)
                .cropId(this.cropId)
                .curriculumItems(this.curriculumItems)
                .build();
        return studyRepository.save(study);
    }

    public static Study algorithmStudy(){
        return Study.builder()
                .name("알고리즘")
                .description("알고리즘 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .teamId(1L)
                .status(StudyStatus.IN_PROGRESS)
                .isDeleted(false)
                .cropId(1L)
                .curriculumItems(new ArrayList<CurriculumItem>())
                .build();
    }
}
