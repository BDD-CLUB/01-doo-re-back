package doore.login.utils;

import doore.login.application.dto.GoogleAccountProfile;
import org.springframework.stereotype.Component;

@Component
public class GoogleClient {

    public GoogleAccountProfile getGoogleAccountProfile(final String code) {
        return new GoogleAccountProfile();
    }
}
