package com.folionmedia.payment.server.api;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaymentResponse implements Serializable{
	
	private static final long serialVersionUID = -8715996798092824423L;

	/**
     * The Type of Payment that this transaction response represents
     */
    protected PaymentType paymentType;
    
    /**
     * The Type of Payment Vendor that this transaction response represents
     */
    protected PaymentVendorType paymentVendorType;

    /**
     * The Transaction Type of the Payment that this response represents
     */
    protected PaymentTransactionType paymentTransactionType;

    /**
     * The Customer ID that this transaction is associated with
     */
    protected String uid;
    /**
     * The Tx ID that this transaction is associated with
     */
    protected String txId;
    /**
     * The vendor specific information
     */
    protected String token;
    protected String vendorPayerId;
    protected String vendorTxId;
    
    /**
     * product information
     */
    private String productId;
    
	private String productName;
	
	private String productDescription;
	
	private int productQuantity = -1;
	
	private String productUnitPrice = "0";
	
	private String productTotalAmount = "0";
	
	protected String totalAmount;
	
	protected String currencyCode;
    
    /**
     * Whether or not the transaction on the gateway was successful. This should be provided by the gateway alone.
     */
    protected boolean successful = true;
    
    /**
     * Whether or not this response was tampered with. This used to verify that the response that was received on the
     * endpoint (which is intended to only be invoked from the payment gateway) actually came from the gateway and was not
     * otherwise maliciously invoked by a 3rd-party. 
     */
    protected boolean valid = true;

    /**
     * <p>Sets whether or not this module should complete checkout on callback.
     * In most Credit Card gateway implementation, this should be set to 'TRUE' and
     * should not be configurable as the gateway expects it to tbe the final step
     * in the checkout process.</p>
     *
     * <p>In gateways where it does not expect to be the last step in the checkout process,
     * for example BLC Gift Card Module, PayPal Express Checkout, etc... The callback from
     * the gateway can be configured whether or not to complete checkout.</p>
     */
    protected boolean completeCheckoutOnCallback = true;
    
    /**
     * A more convenient representation of {@link #rawResponse} to hold the response from the gateway.
     */
    protected Map<String, String> responseMap;

	public PaymentResponse(PaymentType paymentType,
			PaymentVendorType paymentVendorType) {
		super();
		this.paymentType = paymentType;
		this.paymentVendorType = paymentVendorType;
		this.responseMap = new LinkedHashMap<String, String>();
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public PaymentTransactionType getPaymentTransactionType() {
		return paymentTransactionType;
	}

	public void setPaymentTransactionType(
			PaymentTransactionType paymentTransactionType) {
		this.paymentTransactionType = paymentTransactionType;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isCompleteCheckoutOnCallback() {
		return completeCheckoutOnCallback;
	}

	public void setCompleteCheckoutOnCallback(boolean completeCheckoutOnCallback) {
		this.completeCheckoutOnCallback = completeCheckoutOnCallback;
	}

	public Map<String, String> getResponseMap() {
		return responseMap;
	}

	public void setResponseMap(Map<String, String> responseMap) {
		this.responseMap = responseMap;
	}
    
    public PaymentResponse responseMap(String key, String value) {
        responseMap.put(key, value);
        return this;
    }
    
    public String responseMap(String key) {
        return responseMap.get(key);
    }

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
    
    public PaymentVendorType getPaymentVendorType() {
		return paymentVendorType;
	}

	public void setPaymentVendorType(PaymentVendorType paymentVendorType) {
		this.paymentVendorType = paymentVendorType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getVendorPayerId() {
		return vendorPayerId;
	}

	public void setVendorPayerId(String vendorPayerId) {
		this.vendorPayerId = vendorPayerId;
	}

	public String getVendorTxId() {
		return vendorTxId;
	}

	public void setVendorTxId(String vendorTxId) {
		this.vendorTxId = vendorTxId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	public String getProductUnitPrice() {
		return productUnitPrice;
	}

	public void setProductUnitPrice(String productUnitPrice) {
		this.productUnitPrice = productUnitPrice;
	}

	public String getProductTotalAmount() {
		return productTotalAmount;
	}

	public void setProductTotalAmount(String productTotalAmount) {
		this.productTotalAmount = productTotalAmount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	
	
}
