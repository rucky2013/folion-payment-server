folion-payment-server
=====================

Quick Start
------------

Start up the ActiveMQ server and Couchbase server.

Import the code as a maven project.

Run the project as Java application with main class "ServerApplication"

Access this url from your browser: localhost:8080/paypal-express/checkout

Input the paypal sandbox account : paypal_buyer_usa_01@gmail.com/paypal_buyer_usa_01

Checkout the return response, and the Couchbase bucket "payment" for the successful transaction.


Integraion
------------

### Prequisites

include the maven dependency to your main pom.xml:

		<dependency>
			<groupId>com.folionmedia.payment</groupId>
			<artifactId>folion-payment-server</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>

### Access the service via JMS

declare the service stub isntance using Spring JMS Remoting

	<bean id="paymentService" class="org.springframework.jms.remoting.JmsInvokerProxyFactoryBean">
		<property name="serviceInterface" value="com.folionmedia.payment.server.api.PaymentService" />
		<property name="connectionFactory" ref="connectionFactory" />
		<property name="queue" ref="requestQueue" />
	</bean>

to use the service, simple declare a service dependency in your class.

	@Autowired
	@Qualifier("paymentService")
	private PaymentService paymentService;

### Service API

the core service interface is "PaymentService", it contains three methods to deal with paypal payment process.

	PaymentResponse requestHostedEndpoint(PaymentRequest paymentRequest) throws PaymentException;
	
	PaymentResponse applyPaymentToTransaction(PaymentRequest paymentRequest) throws PaymentException;
	
	PaymentResponse cancelPaymentToTransaction(PaymentRequest paymentRequest) throws PaymentException;

Method "requestHostedEndpoint" provides the redirect url to paypal payment page when user click "pay with paypal" button.

Method "applyPaymentToTransaction" handles the paypay payment redirect url after user successfully finish the payment on paypal, it will allow you to finalize the payment with paypal and process your own business logic on payment done.

Method "cancelPaymentToTransaction" handles the paypay payment redirect url after user cancel the payment on paypal.



