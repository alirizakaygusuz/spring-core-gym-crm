package util;

import com.alirizakaygusuz.gymcrm.config.app.AppConfig;
import com.alirizakaygusuz.gymcrm.config.persistence.DataSourceConfig;
import com.alirizakaygusuz.gymcrm.config.persistence.JpaConfig;
import com.alirizakaygusuz.gymcrm.config.persistence.TransactionConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceSmokeTest {

    @Test
    void shouldConnectToDatabase() throws Exception {
        try (AnnotationConfigApplicationContext ctx =
                     new AnnotationConfigApplicationContext(
                             AppConfig.class,
                             DataSourceConfig.class,
                             JpaConfig.class,
                             TransactionConfig.class
                     )) {

            DataSource ds = ctx.getBean(DataSource.class);
            try (Connection c = ds.getConnection()) {
                assertThat(c.isValid(2)).isTrue();
            }
        }
    }
}
