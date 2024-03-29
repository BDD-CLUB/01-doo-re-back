package doore.garden.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum GardenType {
    DOCUMENT_UPLOAD("Document", ContributionGarden::new),
    STUDY_CURRICULUM_COMPLETION("ParticipantCurriculumItem", ContributionGarden::new);

    private final String contributionClass;
    private final Supplier<ContributionGarden> supplier;
    private static final Map<String, GardenType> typeMap = Arrays.stream(GardenType.values())
            .collect(Collectors.toMap(GardenType::getContributionClass, gardenType -> gardenType));

    GardenType(String contributionClass, Supplier<ContributionGarden> supplier) {
        this.contributionClass = contributionClass;
        this.supplier = supplier;
    }

    public static GardenType getGardenTypeOf(String contributionClass) {
        return typeMap.get(contributionClass);
    }

    public static ContributionGarden getSupplierOf(String contributionClass) {
        return getGardenTypeOf(contributionClass).getSupplier().get();
    }
}
