package doore.study.application;

import static doore.study.exception.CurriculumItemExceptionType.NOT_FOUND_CURRICULUM_ITEM;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

import doore.helper.IntegrationTest;
import doore.study.application.dto.request.CurriculumItemRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.CurriculumItemRepository;
import doore.study.exception.CurriculumItemException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CurriculumItemCommandServiceTest extends IntegrationTest {

    @Autowired
    private CurriculumItemCommandService curriculumItemCommandService;
    @Autowired
    protected CurriculumItemRepository curriculumItemRepository;

    @Test
    @DisplayName("존재하지 않는 커리큘럼을 삭제할 수 없다. [실패]")
    public void 존재하지_않는_커리큘럼을_삭제할_수_없다_실패() throws Exception {
        Long studyId = 1L; // StudyFixture 생성 후 수정할 부분
        Long invalidCurriculumItemId = 5L;

        assertThatThrownBy(() -> {
            curriculumItemCommandService.deleteCurriculum(invalidCurriculumItemId, studyId);
        }).isInstanceOf(CurriculumItemException.class).hasMessage(NOT_FOUND_CURRICULUM_ITEM.errorMessage());
    }

    @Test
    @DisplayName("존재하지 않는 커리큘럼을 수정할 수 없다. [실패]")
    public void 존재하지_않는_커리큘럼을_수정할_수_없다_실패() throws Exception {
        Long studyId = 1L; // StudyFixture 생성 후 수정할 부분
        Long invalidCurriculumItemId = 5L;
        CurriculumItemRequest request = CurriculumItemRequest.builder().name("스프링 MVC 이해").build();

        assertThatThrownBy(() -> {
            curriculumItemCommandService.updateCurriculum(invalidCurriculumItemId, studyId, request);
        }).isInstanceOf(CurriculumItemException.class).hasMessage(NOT_FOUND_CURRICULUM_ITEM.errorMessage());
    }

    @Test
    @DisplayName("커리큘럼을 삭제하면 아이템순서가 수정된다. [성공]")
    public void 커리큘럼을_삭제하면_아이템순서가_수정된다() throws Exception {
        Long studyId = 1L; // StudyFixture 생성 후 수정할 부분
        List<String> curriculumNames = Arrays.asList("스프링 MVC 이해1", "스프링 MVC 이해2", "스프링 MVC 이해3");
        curriculumNames.forEach(name -> curriculumItemCommandService.createCurriculum(
                CurriculumItemRequest.builder().name(name).build(), studyId));
        List<CurriculumItem> curriculumItemList = curriculumItemRepository.findAll();

        curriculumItemCommandService.deleteCurriculum(curriculumItemList.get(1).getId(), studyId);
        List<CurriculumItem> result = curriculumItemRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(1).getItemOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 커리큘럼의 완료 상태를 변경할 수 없다. [실패]")
    public void 존재하지_않는_커리큘럼의_완료_상태를_변경할_수_없다() throws Exception {
        Long studyId = 1L; // StudyFixture 생성 후 수정할 부분
        Long invalidCurriculumItemId = 5L;

        assertThatThrownBy(() -> {
            curriculumItemCommandService.completeCurriculum(invalidCurriculumItemId, studyId);
        }).isInstanceOf(CurriculumItemException.class).hasMessage(NOT_FOUND_CURRICULUM_ITEM.errorMessage());
    }
}
