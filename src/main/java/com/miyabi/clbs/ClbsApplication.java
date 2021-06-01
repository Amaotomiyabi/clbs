package com.miyabi.clbs;

import com.miyabi.clbs.reptile.Reptile;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * @author amotomiyabi
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.miyabi.clbs.dao"})
@EnableElasticsearchRepositories(basePackages = {"com.miyabi.clbs.esdao"})
public class ClbsApplication implements CommandLineRunner {

    private final Reptile reptile;

    public ClbsApplication(Reptile reptile) {
        this.reptile = reptile;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClbsApplication.class, args);
    }

    @Override
    public void run(String... args) {
        //reptile.initReptile();
    }
}
