package doore.login.application.dto.response;

public record LoginResponse(
        Long memberId,
        String token
) {
}
