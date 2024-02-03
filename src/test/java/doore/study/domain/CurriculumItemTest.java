package doore.study.domain;

import static org.assertj.core.api.Assertions.assertThat;

import doore.study.CurriculumItemFixture;
import doore.study.domain.repository.CurriculumItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CurriculumItemTest {

    @Autowired
    private CurriculumItemRepository curriculumItemRepository;

    @Test
    @DisplayName("[성공] 커리큘럼이 정상 수정된다.")
    public void update_커리큘럼이_정상_수정된다_성공() {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();

        String changeName = "변경된 Spring 스터디";
        curriculumItem.update(changeName);

        assertThat(curriculumItem.getName()).isEqualTo(changeName);
    }

    @Test
    @DisplayName("[성공] 커리큘럼이 완료 상태로 변경된다.")
    public void complete_커리큘럼이_완료_상태로_변경된다_성공() {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .participantId(1L)
                .curriculumItem(curriculumItem)
                .build();

        participantCurriculumItem.complete();

        assertThat(participantCurriculumItem.getIsChecked()).isEqualTo(true);
    }

    @Test
    @DisplayName("[성공] 커리큘럼이 미완료 상태로 변경된다.")
    public void incomplete_커리큘럼이_미완료_상태로_변경된다_성공() {
        CurriculumItem curriculumItem = CurriculumItemFixture.curriculumItem();
        ParticipantCurriculumItem participantCurriculumItem = ParticipantCurriculumItem.builder()
                .participantId(1L)
                .curriculumItem(curriculumItem)
                .build();

        participantCurriculumItem.incomplete();

        assertThat(participantCurriculumItem.getIsChecked()).isEqualTo(false);
    }
}
