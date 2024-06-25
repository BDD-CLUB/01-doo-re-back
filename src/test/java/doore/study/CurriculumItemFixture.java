package doore.study;

import static doore.study.StudyFixture.algorithmStudy;
import static doore.study.StudyFixture.createStudy;

import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import org.springframework.stereotype.Component;

@Component
public class CurriculumItemFixture {

    public static CurriculumItem curriculumItem() {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(createStudy())
                .build();
    }

    public static CurriculumItem deleteCurriculumItem() {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(algorithmStudy())
                .build();
    }

    public static CurriculumItem curriculumItem(final Study study) {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(study)
                .build();
    }

    public static CurriculumItem deleteCurriculumItem(final Study study) {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(study)
                .build();
    }
}
