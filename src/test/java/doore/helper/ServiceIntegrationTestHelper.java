package doore.helper;

import doore.login.utils.GoogleClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public abstract class ServiceIntegrationTestHelper {
    @MockBean
    protected GoogleClient googleClient;
}
