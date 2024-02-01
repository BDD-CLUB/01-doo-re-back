package doore.study.application;

import static doore.study.exception.CurriculumItemExceptionType.NOT_FOUND_CURRICULUM_ITEM;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import doore.helper.IntegrationTest;
import doore.study.StudyFixture;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.domain.Study;
import doore.study.domain.repository.CurriculumItemRepository;
import doore.study.domain.repository.StudyRepository;
import doore.study.exception.CurriculumItemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CurriculumItemCommandServiceTest extends IntegrationTest {

    @Autowired
    private CurriculumItemCommandService curriculumItemCommandService;
    @Autowired
    protected CurriculumItemRepository curriculumItemRepository;
    @Autowired
    protected StudyRepository studyRepository;

    private Study study;
    private Long invalidCurriculumItemId;

    @BeforeEach
    void setUp() {
        study = StudyFixture.algorithmStudy();
        studyRepository.save(study);
        invalidCurriculumItemId = 5L;
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 커리큘럼을 삭제할 수 없다.")
    public void deleteCurriculum_존재하지_않는_커리큘럼을_삭제할_수_없다_실패() throws Exception {
        assertThatThrownBy(() -> {
            curriculumItemCommandService.deleteCurriculum(invalidCurriculumItemId, study.getId());
        }).isInstanceOf(CurriculumItemException.class).hasMessage(NOT_FOUND_CURRICULUM_ITEM.errorMessage());
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 커리큘럼을 수정할 수 없다.")
    public void updateCurriculum_존재하지_않는_커리큘럼을_수정할_수_없다_실패() throws Exception {
        CurriculumItemRequest request = new CurriculumItemRequest("Spring Study");

        assertThatThrownBy(() -> {
            curriculumItemCommandService.updateCurriculum(invalidCurriculumItemId, study.getId(), request);
        }).isInstanceOf(CurriculumItemException.class).hasMessage(NOT_FOUND_CURRICULUM_ITEM.errorMessage());
    }

//    @Test
//    @DisplayName("[성공] 커리큘럼을 삭제하면 아이템순서가 수정된다.")
//    public void deleteCurriculum_커리큘럼을_삭제하면_아이템순서가_수정된다() throws Exception {
//        List<String> curriculumNames = Arrays.asList("스프링 MVC 이해1", "스프링 MVC 이해2", "스프링 MVC 이해3");
//        curriculumNames.forEach(name -> curriculumItemCommandService.createCurriculum(
//                CurriculumItemRequest.builder().name(name).build(), study.getId()));
//        List<CurriculumItem> curriculumItemList = curriculumItemRepository.findAll();
//
//        curriculumItemCommandService.deleteCurriculum(curriculumItemList.get(1).getId(), study.getId());
//        List<CurriculumItem> result = curriculumItemRepository.findAll();
//
//        assertThat(result).hasSize(2);
//        assertThat(result.get(1).getItemOrder()).isEqualTo(2);
//    }

    @Test
    @DisplayName("[실패] 존재하지 않는 커리큘럼의 완료 상태를 변경할 수 없다.")
    public void completeCurriculum_존재하지_않는_커리큘럼의_완료_상태를_변경할_수_없다() throws Exception {
        assertThatThrownBy(() -> {
            curriculumItemCommandService.completeCurriculum(invalidCurriculumItemId, study.getId());
        }).isInstanceOf(CurriculumItemException.class).hasMessage(NOT_FOUND_CURRICULUM_ITEM.errorMessage());
    }
}
