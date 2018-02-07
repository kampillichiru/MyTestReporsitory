package com.chiru.springboot.configuration;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.chiru.bean.DMDEnv;
import com.chiru.springboot.condition.ConditionPrimary;
import com.chiru.springboot.condition.ConditionSecondary;
import com.chiru.springboot.util.DMDUtil;
import com.chiru.springboot.util.DmdLogger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "com.att.nsa.dmd.springboot.configuration" })
public class HibernateConfiguration {
	static private final DmdLogger logger = DmdLogger.getLogger(
			HibernateConfiguration.class, DmdLogger.DMD_API);
	@Autowired
	private Environment environment;
	@Autowired(required = false)
	@Qualifier("datasourcePrimary")
	private ComboPooledDataSource datasourcePrimary;
	@Autowired(required = false)
	@Qualifier("datasourceSecondary")
	private ComboPooledDataSource datasourceSecondary;

	@Bean
	public DMDEnv dmdEnv() {
		DMDEnv dmdEnv = new DMDEnv();
		String dmdEnvAttr = environment.getProperty("dmd.env");
		String envs[] = dmdEnvAttr.split(",");
		String applicationURL = environment.getProperty("applicationURL");
		Map<String, String> dmdenvMap = new HashMap<String, String>();

		for (String env : envs) {
			dmdenvMap.put(env, DMDUtil.getLiveHostName(environment, env + ".HOSTNAMES", applicationURL));

		}
		dmdEnv.setDmdenv(dmdenvMap);
		String env =environment.getProperty("spring.profiles.active");
		
			dmdEnv.setEnv(env);
		dmdEnv.setEnvironment(environment);
		logger.debug("DMD Environemnt attributes setup: "+dmdEnv.toString());
		return dmdEnv;
	}


	private Properties hibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
		return properties;
	}
	@Primary
	@Bean(name = "sessionFactoryPrimary")
	@Qualifier("sessionFactoryPrimary")
	@Conditional(ConditionPrimary.class)
	public LocalSessionFactoryBean sessionFactoryPrimary() throws UnknownHostException {
		logger.debug("Creating sessionFactoryPrimary with datasourcePrimary: "+datasourcePrimary);
		LocalSessionFactoryBean sessionFactory = null;
		sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(datasourcePrimary);
		sessionFactory.setPackagesToScan(new String[] { "com.att.nsa.dmd.springboot.model" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}

	@Bean(name = "sessionFactorySeconday")
	@Qualifier("sessionFactorySeconday")
	@Conditional(ConditionSecondary.class)
	public LocalSessionFactoryBean sessionFactorySeconday() throws UnknownHostException {
		logger.debug("Creating sessionFactorySeconday with datasourceSecondary: "+datasourceSecondary);
		LocalSessionFactoryBean sessionFactory = null;
		sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(datasourceSecondary);
		sessionFactory.setPackagesToScan(new String[] { "com.att.nsa.dmd.springboot.model" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}

}
