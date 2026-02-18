package com.alirizakaygusuz.gymcrm.config.persistence;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {

    @Bean
    public DataSource dataSource(
            @Value("${db.url}") String url,
            @Value("${db.username}") String username,
            @Value("${db.password}") String password
    ) {
        var ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("org.postgresql.Driver");


        ds.setMaximumPoolSize(10);
        ds.setPoolName("gymcrm-hikari");


        return ds;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }


    @Bean
    @DependsOn("flyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            @Value("${hibernate.hbm2ddl.auto}") String ddlAuto,
            @Value("${hibernate.show_sql}") boolean showSql,
            @Value("${hibernate.format_sql}") boolean formatSql,
            LocalValidatorFactoryBean validatorFactory
    ) {
        var emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);

        emf.setPackagesToScan("com.alirizakaygusuz.gymcrm.model");

        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        var props = new Properties();
        props.put("hibernate.hbm2ddl.auto", ddlAuto);
        props.put("hibernate.show_sql", String.valueOf(showSql));
        props.put("hibernate.format_sql", String.valueOf(formatSql));

        props.put("jakarta.persistence.validation.factory", validatorFactory);

        emf.setJpaProperties(props);

        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
