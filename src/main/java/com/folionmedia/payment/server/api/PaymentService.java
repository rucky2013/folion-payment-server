package com.folionmedia.payment.server.api;

public interface PaymentService {
	
	PaymentResponse requestHostedEndpoint(PaymentRequest paymentRequest) throws PaymentException;
	
	PaymentResponse findDetailsByTransaction(PaymentRequest paymentRequest) throws PaymentException;
	
	PaymentResponse authorize(PaymentRequest paymentRequest) throws PaymentException;

	PaymentResponse capture(PaymentRequest paymentRequest) throws PaymentException ;

	PaymentResponse authorizeAndCapture(PaymentRequest paymentRequest) throws PaymentException ;

	PaymentResponse reverseAuthorize(PaymentRequest paymentRequest) throws PaymentException;

	PaymentResponse refund(PaymentRequest paymentRequest) throws PaymentException;

	PaymentResponse voidPayment(PaymentRequest paymentRequest) throws PaymentException;
	
	PaymentResponse applyPaymentToTransaction(PaymentRequest paymentRequest) throws PaymentException;
	
	PaymentResponse cancelPaymentToTransaction(PaymentRequest paymentRequest) throws PaymentException;
}
