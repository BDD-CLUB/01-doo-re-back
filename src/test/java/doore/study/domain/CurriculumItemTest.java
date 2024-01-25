package doore.study.domain;

import doore.study.CurriculumItemFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CurriculumItemTest {

    @Test
    @DisplayName("커리큘럼이 정상 수정된다. [성공]")
    public void 커리큘럼이_정상_수정된다_성공() {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();

        String changeName = "변경된 Spring 스터디";
        curriculumItem.update(changeName);

        assertThat(curriculumItem.getName()).isEqualTo(changeName);
    }
}
