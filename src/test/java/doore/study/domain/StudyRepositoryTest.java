package doore.study.domain;

import static doore.study.domain.StudyStatus.ENDED;
import static doore.study.domain.StudyStatus.IN_PROGRESS;
import static doore.study.domain.StudyStatus.UPCOMING;
import static org.assertj.core.api.Assertions.assertThat;

import static doore.study.StudyFixture.endedStudy;
import static doore.study.StudyFixture.inProgressStudy;
import static doore.study.StudyFixture.upComingStudy;

import doore.helper.RepositorySliceTest;
import doore.study.domain.repository.StudyRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class StudyRepositoryTest extends RepositorySliceTest {

    @Autowired
    private StudyRepository studyRepository;

    @Test
    @DisplayName("스터디 정렬 순서는 진행중-다가오는-끝난 스터디 순이다.")
    public void findAllByTeamIdOrderByStudyStatusNative_스터디_정렬_순서는_진행중_다가오는_끝난_스터디_순이다_성공() {
        final Study endedStudy = studyRepository.save(endedStudy());
        final Study inProgressStudy = studyRepository.save(inProgressStudy());
        final Study upComingStudy = studyRepository.save(upComingStudy());

        em.flush();
        em.clear();

        Page<Study> results = studyRepository.findAllByTeamIdOrderByStudyStatusNative(endedStudy.getTeamId(),
                PageRequest.of(0, 4));
        List<Study> sortedStudies = results.getContent();

        assertThat(sortedStudies.get(0).getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(sortedStudies.get(1).getStatus()).isEqualTo(UPCOMING);
        assertThat(sortedStudies.get(2).getStatus()).isEqualTo(ENDED);
    }

}
