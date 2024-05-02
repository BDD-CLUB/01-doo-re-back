package doore.garden.domain;

import java.util.function.Supplier;
import lombok.Getter;

@Getter
public enum GardenType {
    DOCUMENT_UPLOAD("Document",ContributionGardens::new),
    COMPLETE_STUDY_CURRICULUM("ParticipantCurriculumItem", ContributionGardens::new);

    private final String type;
    private final Supplier<GardenInterface> supplier;

    GardenType(String type, Supplier<GardenInterface> supplier) {
        this.type = type;
        this.supplier = supplier;
    }
}
