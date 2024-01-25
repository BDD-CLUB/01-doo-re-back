package doore.login.application.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleAccountProfileResponse(
        String id,
        String email,
        @JsonProperty("verified_email")
        Boolean verifiedEmail,
        String name,
        @JsonProperty("given_name")
        String givenName,
        @JsonProperty("family_name")
        String familyName,
        String picture,
        String locale
) {
}
