package doore.study.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import doore.helper.RepositorySliceTest;
import doore.study.domain.repository.ParticipantCurriculumItemRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParticipantCurriculumItemRepositoryTest extends RepositorySliceTest {
    @Autowired
    ParticipantCurriculumItemRepository participantCurriculumItemRepository;

    final Long teamId = 1L;

    @Test
    @DisplayName("오늘 체크된 팀의 커리큘럼의 개수를 계산할 수 있다.")
    public void getTodayCheckedCurriculumItemFromTeam_오늘_체크된_팀의_커리큘럼의_개수를_계산할_수_있다_성공() throws Exception {
        //when
        List<ParticipantCurriculumItem> participantCurriculumItems = participantCurriculumItemRepository.findAll();
        int countedCheckedCurriculumItem =
                participantCurriculumItemRepository.getTodayCheckedCurriculumItemFromTeam(teamId);
        //then
        assertEquals(5,participantCurriculumItems.size());
        assertEquals(1, countedCheckedCurriculumItem);
    }

    @Test
    @DisplayName("이번주에 체크된 팀의 커리큘럼의 개수를 계산할 수 있다.")
    public void getWeekCheckedCurriculumItemFromTeam_이번주에_체크된_팀의_커리큘럼의_개수를_계산할_수_있다_성공() throws Exception {
        //when
        List<ParticipantCurriculumItem> participantCurriculumItems = participantCurriculumItemRepository.findAll();
        int countedCheckedCurriculumItem =
                participantCurriculumItemRepository.getWeekCheckedCurriculumItemFromTeam(teamId);
        //then
        assertEquals(5,participantCurriculumItems.size());
        assertEquals(2, countedCheckedCurriculumItem);
    }
}
