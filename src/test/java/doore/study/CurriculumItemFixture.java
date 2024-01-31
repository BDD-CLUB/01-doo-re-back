package doore.study;

import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CurriculumItemFixture {
    private static StudyRepository studyRepository;

    @Autowired
    public CurriculumItemFixture(StudyRepository studyRepository) {
        CurriculumItemFixture.studyRepository = studyRepository;
    }

    public static Study createStudy() {
        return studyRepository.save(StudyFixture.algorithmStudy());
    }

    public static CurriculumItem curriculumItem() {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(createStudy())
                .isDeleted(false)
                .build();
    }

    public static CurriculumItem deleteCurriculumItem() {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(StudyFixture.algorithmStudy())
                .isDeleted(true)
                .build();
    }
}
