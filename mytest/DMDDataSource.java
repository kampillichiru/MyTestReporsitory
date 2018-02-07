package com.chiru.springboot.datasource;

import java.beans.PropertyVetoException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import com.chiru.springboot.condition.ConditionPrimary;
import com.chiru.springboot.condition.ConditionSecondary;
import com.chiru.springboot.util.DmdLogger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
public class DMDDataSource {
	
	static private final DmdLogger logger = DmdLogger.getLogger(
			DMDDataSource.class, DmdLogger.DMD_API);
	@Autowired
	private Environment environment;
	
	@Primary
	@Bean
	@Qualifier("datasourcePrimary")
	@Conditional(ConditionPrimary.class)
    public ComboPooledDataSource datasourcePrimary() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource("PrimaryDS");
        String env=environment.getProperty("spring.profiles.active").toLowerCase();
        try {
        	logger.debug("Creating datasourcePrimary");
            dataSource.setDriverClass(environment.getProperty(env+".datasource.db.primary.driver"));
        } catch (PropertyVetoException pve){
            logger.error("Cannot load datasource driver (" + environment.getProperty(env+".datasource.db.primary.driver") +") : " + pve.getMessage());
            return null;
        }
        dataSource.setJdbcUrl(environment.getProperty(env+".datasource.db.primary.url"));
        dataSource.setUser(environment.getProperty(env+".datasource.db.primary.username"));
        dataSource.setPassword(environment.getProperty(env+".datasource.db.primary.password"));
        dataSource.setMinPoolSize(Integer.parseInt(environment.getProperty(env+".datasource.db.primary.hibernate.c3p0.min_size")));
        dataSource.setMaxPoolSize(Integer.parseInt(environment.getProperty(env+".datasource.db.primary.hibernate.c3p0.max_size")));
        dataSource.setMaxIdleTime(Integer.parseInt(environment.getProperty(env+".datasource.db.primary.hibernate.c3p0.idle_test_period")));
        dataSource.setMaxStatements(Integer.parseInt(environment.getProperty(env+".datasource.db.primary.hibernate.c3p0.max_statements")));

        return dataSource;
    }
	
	@Bean
	@Qualifier("datasourceSecondary")
	@Conditional(ConditionSecondary.class)
    public ComboPooledDataSource datasourceSecondary() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource("SecondaryDS");
        String env=environment.getProperty("spring.profiles.active").toLowerCase();
        try {
        	logger.debug("Creating datasourceSecondary");
            dataSource.setDriverClass(environment.getProperty(env+".datasource.db.seconday.driver"));
        } catch (PropertyVetoException pve){
        	logger.error("Cannot load datasource driver (" + environment.getProperty(env+".datasource.db.secondary.driver") +") : " + pve.getMessage());
            return null;
        }
        dataSource.setJdbcUrl(environment.getProperty(env+".datasource.db.secondary.url"));
        dataSource.setUser(environment.getProperty(env+".datasource.db.secondary.username"));
        dataSource.setPassword(environment.getProperty(env+".datasource.db.secondary.password"));
        dataSource.setMinPoolSize(Integer.parseInt(environment.getProperty(env+".datasource.db.secondary.hibernate.c3p0.min_size")));
        dataSource.setMaxPoolSize(Integer.parseInt(environment.getProperty(env+".datasource.db.secondary.hibernate.c3p0.max_size")));
        dataSource.setMaxIdleTime(Integer.parseInt(environment.getProperty(env+".datasource.db.secondary.hibernate.c3p0.idle_test_period")));
        dataSource.setMaxStatements(Integer.parseInt(environment.getProperty(env+".datasource.db.primary.hibernate.c3p0.max_statements")));
        return dataSource;
    }
}