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

    @Test
    @DisplayName("커리큘럼이 완료 상태로 변경된다. [성공]")
    public void 커리큘럼이_완료_상태로_변경된다_성공() {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .isChecked(false).isDeleted(false).participantId(1L).curriculumItem(curriculumItem).build();

        participantCurriculumItem.complete();

        assertThat(participantCurriculumItem.getIsChecked()).isEqualTo(true);
    }

    @Test
    @DisplayName("커리큘럼이 미완료 상태로 변경된다. [성공]")
    public void 커리큘럼이_미완료_상태로_변경된다_성공() {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .isChecked(true).isDeleted(false).participantId(1L).curriculumItem(curriculumItem).build();

        participantCurriculumItem.incomplete();

        assertThat(participantCurriculumItem.getIsChecked()).isEqualTo(false);
    }
}
