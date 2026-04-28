package integration.support;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

public class DatabaseCleanupHook {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${workload-service.base-url}")
    private String workloadServiceBaseUrl;

    @Before
    public void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM trainings");
        jdbcTemplate.execute("DELETE FROM trainee_trainer");
        jdbcTemplate.execute("DELETE FROM trainees");
        jdbcTemplate.execute("DELETE FROM trainers");
        jdbcTemplate.execute("DELETE FROM user_roles");
        jdbcTemplate.execute("DELETE FROM users");

        new RestTemplate().delete(workloadServiceBaseUrl + "/api/v1/test/reset");
    }
}