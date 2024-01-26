package doore.study.domain;

import static doore.study.StudyFixture.algorithm_study;
import static doore.study.StudyFixture.studyUpdateRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.study.application.dto.request.StudyUpdateRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StudyTest {

    @Test
    @DisplayName("커리큘럼을 생성할 수 있다.")
    public void 커리큘럼을_생성할_수_있다_성공() {
        final Study study = algorithm_study();
        CurriculumItem curriculumItem1 = CurriculumItem.builder()
                .name("커리큘럼 1단계")
                .itemOrder(1)
                .isDeleted(false)
                .study(study)
                .build();
        CurriculumItem curriculumItem2 = CurriculumItem.builder()
                .name("커리큘럼 2단계")
                .itemOrder(2)
                .isDeleted(false)
                .study(study)
                .build();
        List<CurriculumItem> curriculumItems = List.of(curriculumItem1, curriculumItem2);

        study.createCurriculumItems(curriculumItems);
        assertEquals(curriculumItems, study.getCurriculumItems());
    }

    @Test
    @DisplayName("스터디의 내용을 변경할 수 있다.")
    public void 스터디의_내용을_변경할_수_있다_성공() {
        final Study study = algorithm_study();
        StudyUpdateRequest request = studyUpdateRequest();
        study.update(request.name(), request.description(), request.startDate(), request.endDate(), request.status());
        assertEquals(study.getName(), request.name());
        assertEquals(study.getDescription(), request.description());
    }

}
