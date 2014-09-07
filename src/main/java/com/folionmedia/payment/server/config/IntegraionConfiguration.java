package com.folionmedia.payment.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"activemq-config.xml", "payment-server.xml"})
public class IntegraionConfiguration {

}
