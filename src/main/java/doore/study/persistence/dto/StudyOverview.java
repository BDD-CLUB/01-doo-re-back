package doore.study.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StudyOverview {
    StudyInformation studyInformation;
    Long curriculumId;
    String curriculumName;
    Integer curriculumItemOrder;
    Boolean curriculumItemIsDeleted;
}
