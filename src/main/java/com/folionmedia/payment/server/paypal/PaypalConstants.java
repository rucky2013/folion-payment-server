package com.folionmedia.payment.server.paypal;

import com.folionmedia.payment.server.api.PaymentConstants;

public interface PaypalConstants extends PaymentConstants{
	
	String PAYPAL_API_MODE = "mode";
	
	String PAYPAL_API_USERNAME = "acct1.UserName";
	
	String PAYPAL_API_PASSWORD = "acct1.Password";
	
	String PAYPAL_API_SIGNATURE = "acct1.Signature";
	
	String PAYPAL_API_APPID = "acct1.AppId";
	
	String PAYPAL_API_ENDPOINT = "service.EndPoint.PayPalAPI";
	
	String TOKEN = "TOKEN";
	
	String REDIRECTURL = "REDIRECTURL";
	
	String CORRELATIONID = "CORRELATIONID";
	
	String TIMESTAMP = "TIMESTAMP";
	
	
	String PAYPAL_NO_SHIPPINT_VALUE = "1";
}
