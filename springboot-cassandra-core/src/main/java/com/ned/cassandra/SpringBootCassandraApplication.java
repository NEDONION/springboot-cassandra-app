package com.ned.cassandra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.ned.cassandra", "com.ned.log"}) // scan annotations from other packages
@EnableCassandraRepositories(basePackages = {"com.ned.log.repository", "com.ned.cassandra.repository"}) // enable cassandra API from other packages
public class SpringBootCassandraApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCassandraApplication.class, args);
	}

}
