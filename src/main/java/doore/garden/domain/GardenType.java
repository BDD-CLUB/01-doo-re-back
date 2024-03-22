package doore.garden.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum GardenType {
    DOCUMENT_UPLOAD("Document", ContributionGarden::new),
    COMPLETE_STUDY_CURRICULUM("ParticipantCurriculumItem", ContributionGarden::new);

    private final String type;
    private final Supplier<ContributionGarden> supplier;
    private static final Map<String, GardenType> typeMap = Arrays.stream(GardenType.values())
            .collect(Collectors.toMap(GardenType::getType, gardenType -> gardenType));

    GardenType(String type, Supplier<ContributionGarden> supplier) {
        this.type = type;
        this.supplier = supplier;
    }

    public static ContributionGarden getSupplierOf(String type) {
        return typeMap.get(type).getSupplier().get();
    }
}
