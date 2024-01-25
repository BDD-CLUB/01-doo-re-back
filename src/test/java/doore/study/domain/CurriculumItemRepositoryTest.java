package doore.study.domain;

import static org.assertj.core.api.Assertions.assertThat;

import doore.helper.RepositorySliceTest;
import doore.study.CurriculumItemFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CurriculumItemRepositoryTest extends RepositorySliceTest {

    @Autowired
    private CurriculumItemRepository curriculumItemRepository;
// ******스터디 관련 코드 문제******
//    @Test
//    @DisplayName("삭제된 커리큘럼은 조회되지 않는다. [성공]")
//    public void 삭제된_커리큘럼은_조회되지_않는다() throws Exception {
//        CurriculumItem curriculumItem = CurriculumItemFixture.deleteCurriculumItem();
//        curriculumItemRepository.save(curriculumItem);
//        em.flush();
//        em.clear();
//
//        Optional<CurriculumItem> result = curriculumItemRepository.findById(curriculumItem.getId());
//
//        assertThat(result).isEmpty();
//    }

}
