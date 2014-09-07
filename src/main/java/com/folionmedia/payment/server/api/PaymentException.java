package com.folionmedia.payment.server.api;

public class PaymentException extends RuntimeException{

	public PaymentException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -7302418240907657627L;

}
