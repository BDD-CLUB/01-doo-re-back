package doore.study;

import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyFixture {
    private static StudyRepository studyRepository;

    @Autowired
    public StudyFixture(StudyRepository studyRepository) {
        StudyFixture.studyRepository = studyRepository;
    }

    public static Study createStudy() {
        return studyRepository.save(StudyFixture.algorithmStudy());
    }

    public static Study algorithmStudy() {
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
