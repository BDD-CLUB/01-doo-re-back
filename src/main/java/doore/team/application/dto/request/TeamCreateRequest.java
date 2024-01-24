package doore.team.application.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record TeamCreateRequest(
        @NotNull
        String name,

        @Nullable
        String description
) {
}
