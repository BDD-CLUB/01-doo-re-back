package doore.study;

import static doore.study.StudyFixture.algorithmStudy;

import doore.study.domain.CurriculumItem;

public class CurriculumItemFixture {

    public static CurriculumItem curriculumItem() {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(StudyFixture.builder().studyBuild())
                .build();
    }

    public static CurriculumItem deleteCurriculumItem() {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .itemOrder(1)
                .study(algorithmStudy())
                .build();
    }
}
