package doore.study;

import doore.study.application.dto.request.CurriculumItemsRequest;
import doore.study.application.dto.request.StudyCreateRequest;
import doore.study.application.dto.request.StudyUpdateRequest;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import java.time.LocalDate;
import java.util.ArrayList;

public class StudyFixture {
    public static Study algorithm_study() {
        return Study.builder()
                .name("알고리즘")
                .description("알고리즘 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .teamId(1L)
                .status(StudyStatus.IN_PROGRESS)
                .isDeleted(false)
                .cropId(1L)
                .curriculumItems(new ArrayList<CurriculumItem>())
                .build();
    }

    public static StudyCreateRequest studyCreateRequest() {
        return StudyCreateRequest.builder()
                .name("알고리즘")
                .description("알고리즘 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .status(StudyStatus.IN_PROGRESS)
                .isDeleted(false)
                .cropId(1L)
                .curriculumItems(new ArrayList<CurriculumItemsRequest>())
                .build();
    }

    public static StudyUpdateRequest studyUpdateRequest() {
        return StudyUpdateRequest.builder()
                .name("스프링")
                .description("스프링 스터디 입니다.")
                .startDate(LocalDate.parse("2023-01-01"))
                .endDate(LocalDate.parse("2024-01-01"))
                .status(StudyStatus.IN_PROGRESS)
                .build();
    }
}
