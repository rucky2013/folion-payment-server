package com.folionmedia.payment.server.service;

import com.folionmedia.payment.server.api.PaymentException;
import com.folionmedia.payment.server.api.PaymentRequest;
import com.folionmedia.payment.server.api.PaymentResponse;
import com.folionmedia.payment.server.api.PaymentService;

public class AbstractPaymentService implements PaymentService {
	
	public PaymentResponse requestHostedEndpoint(PaymentRequest paymentRequest)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse findDetailsByTransaction(
			PaymentRequest paymentRequest) throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse authorize(PaymentRequest paymentRequest)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse capture(PaymentRequest paymentRequest)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse authorizeAndCapture(PaymentRequest paymentRequest)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse reverseAuthorize(PaymentRequest paymentRequest)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse refund(PaymentRequest paymentRequest)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse voidPayment(PaymentRequest paymentRequest)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public PaymentResponse applyPaymentToTransaction(
			PaymentRequest paymentRequest) throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentResponse cancelPaymentToTransaction(
			PaymentRequest paymentRequest) throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
