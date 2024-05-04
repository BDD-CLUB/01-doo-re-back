package doore.garden.domain;

import java.util.function.Supplier;
import lombok.Getter;

@Getter
public enum GardenType {
    DOCUMENT_UPLOAD("Document", ContributionGarden::new),
    COMPLETE_STUDY_CURRICULUM("ParticipantCurriculumItem", ContributionGarden::new);

    private final String type;
    private final Supplier<ContributionGarden> supplier;

    GardenType(String type, Supplier<ContributionGarden> supplier) {
        this.type = type;
        this.supplier = supplier;
    }
}
