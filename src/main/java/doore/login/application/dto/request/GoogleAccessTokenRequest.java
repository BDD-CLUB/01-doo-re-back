package doore.login.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class GoogleAccessTokenRequest {
    private final String code;
    @JsonProperty("client_id")
    private final String clientId;
    @JsonProperty("client_secret")
    private final String clentSecret;
    @JsonProperty("redirect_uri")
    private final String redirectUri;
    @JsonProperty("grant_type")
    private final String grantType;

    public GoogleAccessTokenRequest(final String code, final String clentId, final String clientSecret,
                                    final String redirectUrl, final String grantType) {
        this.code = code;
        this.clientId = clentId;
        this.clentSecret = clientSecret;
        this.redirectUri = redirectUrl;
        this.grantType = grantType;
    }
}
