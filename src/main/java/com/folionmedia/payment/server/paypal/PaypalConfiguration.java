package com.folionmedia.payment.server.paypal;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="paypal")
public class PaypalConfiguration {
	
	private String mode;
	
	private String serverUrl;
	
	private String redirectUrl;
	
	private String notifyUrl;
	
	private String password;
	
	private String username;
	
	private String signature;
	
	private String returnUrl;
	
	private String cancelUrl;
	
	public Properties getSDKConfigurationParameters (){
		Properties properties = new Properties();
		properties.setProperty(PaypalConstants.PAYPAL_API_MODE, this.mode);
		properties.setProperty(PaypalConstants.PAYPAL_API_ENDPOINT, this.serverUrl);
		properties.setProperty(PaypalConstants.PAYPAL_API_USERNAME, this.username);
		properties.setProperty(PaypalConstants.PAYPAL_API_PASSWORD, this.password);
		properties.setProperty(PaypalConstants.PAYPAL_API_SIGNATURE, this.signature);
		return properties;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getCancelUrl() {
		return cancelUrl;
	}
	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
