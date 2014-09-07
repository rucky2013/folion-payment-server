package com.folionmedia.payment.server.client;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import com.folionmedia.payment.server.api.PaymentRequest;
import com.folionmedia.payment.server.api.PaymentResponse;
import com.folionmedia.payment.server.api.PaymentService;

@Configuration
@Import(PropertyPlaceholderAutoConfiguration.class)
@ImportResource({"activemq-config.xml", "payment-client.xml"})
public class ClientApplication {

    public static void main(String[] args) {
    	
    	SpringApplication springApplication = new SpringApplication(ClientApplication.class);
    	springApplication.setWebEnvironment(false);
        ConfigurableApplicationContext context = springApplication.run(args);
        PaymentService paymentService = context.getBean(PaymentService.class);
        
    	PaymentRequest paymentRequest = new PaymentRequest();
    	
    	paymentRequest.setUid(UUID.randomUUID().toString());
    	paymentRequest.setProductId(UUID.randomUUID().toString());
    	paymentRequest.setProductName("Product Name");
    	paymentRequest.setProductDescription("Product Description");
    	paymentRequest.setProductQuantity(10);
    	paymentRequest.setProductUnitPrice("2");
    	paymentRequest.setProductTotalAmount("20");
    	
		paymentRequest.setTotalAmount("20");
		paymentRequest.setCurrencyCode("USD");
		paymentRequest.setCompleteCheckoutOnCallback(true);
		
		PaymentResponse paymentResponse = paymentService.requestHostedEndpoint(paymentRequest);
        
		System.out.println("successful? : " + paymentResponse.isSuccessful());
		System.out.println("redirect url? : " + paymentResponse.responseMap("REDIRECTURL"));
		
        context.close();
    }
}
