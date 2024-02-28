package doore.document.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record FileRequest(
        @NotNull(message = "URL을 입력해주세요.")
        String url
) {
}


