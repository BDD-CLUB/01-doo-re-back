package doore.garden.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GardenType {
    DOCUMENT_UPLOAD("Document", Garden::new),
    STUDY_CURRICULUM_COMPLETION("ParticipantCurriculumItem", Garden::new);

    private final String contributionClass;
    private final Supplier<Garden> supplier;
    private static final Map<String, GardenType> typeMap = Arrays.stream(GardenType.values())
            .collect(Collectors.toMap(GardenType::getContributionClass, gardenType -> gardenType));

    public static GardenType getGardenTypeOf(String contributionClass) {
        return typeMap.get(contributionClass);
    }

    public static Garden getSupplierOf(String contributionClass) {
        return getGardenTypeOf(contributionClass).getSupplier().get();
    }
}
