package doore.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = "/data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public abstract class RepositorySliceTest {

    @PersistenceContext
    protected EntityManager em;

}
