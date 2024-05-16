package doore.study.application.dto.response;

public record PersonalCurriculumItemResponse (
        Long id,
        String name,
        Integer itemOrder,
        Boolean isDeleted,
        Boolean isChecked
) {
}
