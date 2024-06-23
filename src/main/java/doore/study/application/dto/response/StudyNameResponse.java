package doore.study.application.dto.response;

import doore.study.domain.Study;

public record StudyNameResponse(
        Long id,
        String name
) {
    public static StudyNameResponse from(final Study study) {
        return new StudyNameResponse(study.getId(), study.getName());
    }
}
