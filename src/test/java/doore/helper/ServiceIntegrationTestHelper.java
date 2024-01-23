package doore.helper;

import doore.login.utils.GoogleClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
@Sql(value = "/clean.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class ServiceIntegrationTestHelper {
    @MockBean
    protected GoogleClient googleClient;
}
