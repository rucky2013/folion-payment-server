package com.folionmedia.payment.server.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.folionmedia.payment.server.config.ApplicationConfiguration;
import com.folionmedia.payment.server.config.CouchbaseConfiguration;
import com.folionmedia.payment.server.config.IntegraionConfiguration;

//@EnableAutoConfiguration
@Import({
		PropertyPlaceholderAutoConfiguration.class,
		ApplicationConfiguration.class,
		IntegraionConfiguration.class,
		CouchbaseConfiguration.class})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
