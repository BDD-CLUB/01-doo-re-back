package doore.login.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleAccessTokenRequest(
        String code,
        @JsonProperty("client_id")
        String clientId,
        @JsonProperty("client_secret")
        String clentSecret,
        @JsonProperty("redirect_uri")
        String redirectUri,
        @JsonProperty("grant_type")
        String grantType
) {
}
