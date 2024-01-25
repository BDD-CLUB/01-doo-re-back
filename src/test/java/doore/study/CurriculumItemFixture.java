package doore.study;

import doore.study.domain.CurriculumItem;

public class CurriculumItemFixture {

    public static CurriculumItem curriculumItem() {
        return CurriculumItem.builder()
                .name("Spring MVC 이해")
                .isDeleted(false)
                .build();
    }

    // ******스터디 관련 코드 문제******
//    public static CurriculumItem deleteCurriculumItem(StudyRepository studyRepository) {
//        return CurriculumItem.builder()
//                .name("Spring MVC 이해")
//                .itemOrder(1)
//                .study(study)
//                .isDeleted(true)
//                .build();
//    }
}
