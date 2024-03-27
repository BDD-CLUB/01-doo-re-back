package doore.login.utils;

import static doore.login.exception.LoginExceptionType.INVALID_ACCESS_TOKEN;
import static doore.login.exception.LoginExceptionType.NOT_FOUND_AUTHORIZATION_TOKEN;

import doore.login.exception.LoginException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class AuthorizationExtractor {
    private static final String ACCESS_TOKEN_TYPE = AuthorizationExtractor.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";
    private static final String BEARER_TYPE = "Bearer";

    public static String extract(final HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        validateAuthorizationHeader(authorizationHeader);

        String authHeaderValue = authorizationHeader.substring(BEARER_TYPE.length()).trim();

        request.setAttribute(ACCESS_TOKEN_TYPE, BEARER_TYPE);
        final int commaIndex = authHeaderValue.indexOf(',');
        if (commaIndex > 0) {
            authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }
        return authHeaderValue;
    }

    private static void validateAuthorizationHeader(final String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new LoginException(NOT_FOUND_AUTHORIZATION_TOKEN);
        }
        if (!authorizationHeader.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            throw new LoginException(INVALID_ACCESS_TOKEN);
        }
    }
}
