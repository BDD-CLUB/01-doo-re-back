package doore.study.application.dto.response.personalStudyResponse;

public record PersonalCurriculumItemResponse (
        Long id,
        String name,
        Integer itemOrder,
        Boolean isDeleted,
        Boolean isChecked
) {
}
