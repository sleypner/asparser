package com.sleypner.parserarticles.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    Environment env;

    @Autowired
    DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.sleypner.parserarticles.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String driverName = null;
        if (env.getProperty("db.url").toLowerCase().indexOf(":sqlite:") > 0) {
            driverName = "org.sqlite.JDBC";
        } else if (env.getProperty("db.url").toLowerCase().indexOf(":mysql:") > 0) {
            driverName = "com.mysql.cj.jdbc.Driver";
        } else if (env.getProperty("db.url").toLowerCase().indexOf(":mariadb:") > 0) {
            driverName = "org.mariadb.jdbc.Driver";
        } else if (env.getProperty("db.url").toLowerCase().indexOf(":postgresql:") > 0) {
            driverName = "org.postgresql.Driver";
        }

        assert driverName != null;
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean
    public static PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        if (env.getProperty("db.url").indexOf(":sqlite:") > 0) {
            properties.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        } else if (env.getProperty("db.url").indexOf(":mysql:") > 0) {
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        } else if (env.getProperty("db.url").indexOf(":mariadb:") > 0) {
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect");
        }else if (env.getProperty("db.url").indexOf(":postgresql:") > 0) {
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        }
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        return properties;
    }
}
