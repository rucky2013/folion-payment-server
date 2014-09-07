package com.folionmedia.payment.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;

import com.folionmedia.payment.server.paypal.PaypalConfiguration;

@Configuration
@ComponentScan("com.folionmedia.payment.server.paypal")
@EnableConfigurationProperties(PaypalConfiguration.class)
public class ApplicationConfiguration {

	@Autowired
	private PaypalConfiguration paypalConfiguration;
	
	@Bean
	public PayPalAPIInterfaceServiceService paypalAPIInterfaceServiceService() {
		return new PayPalAPIInterfaceServiceService(paypalConfiguration.getSDKConfigurationParameters());
	}
}
