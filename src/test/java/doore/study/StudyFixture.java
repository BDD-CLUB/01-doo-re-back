package doore.study;

import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.study.domain.repository.StudyRepository;
import doore.team.TeamFixture;
import doore.team.domain.TeamRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudyFixture {
    private static StudyRepository studyRepository;
    private static TeamRepository teamRepository;
    private static Long teamId = 1L;

    @Autowired
    public StudyFixture(StudyRepository studyRepository, TeamRepository teamRepository) {
        StudyFixture.studyRepository = studyRepository;
        StudyFixture.teamRepository = teamRepository;
    }

    public static Study createStudy() {
        teamId = teamRepository.save(TeamFixture.team()).getId();
        return studyRepository.save(StudyFixture.algorithmStudy());
    }

    public static Study algorithmStudy() {
        return Study.builder()
                .name("알고리즘")
                .description("알고리즘 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .teamId(teamId)
                .status(StudyStatus.IN_PROGRESS)
                .isDeleted(false)
                .cropId(1L)
                .curriculumItems(new ArrayList<CurriculumItem>())
                .build();
    }
}
