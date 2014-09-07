package com.folionmedia.payment.server.api;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


public class PaymentRequest implements Serializable{

	private static final long serialVersionUID = 5282927142780572750L;

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
	 * Subscription
	 */
//    protected String recurringAmount;
//    
//    protected String frequency;
//    
//    protected String numberOfInstallments;
//    
//    protected String startDate;
    
    
    protected Map<String, Object> additionalFields;
    
    /**
     * should the payment completed on payment vendor callback
     */
    protected boolean completeCheckoutOnCallback = true;
    
    public PaymentRequest(){
    	additionalFields = new LinkedHashMap<String, Object>();
    }
    
    public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public PaymentVendorType getPaymentVendorType() {
		return paymentVendorType;
	}

	public void setPaymentVendorType(PaymentVendorType paymentVendorType) {
		this.paymentVendorType = paymentVendorType;
	}

	public PaymentTransactionType getPaymentTransactionType() {
		return paymentTransactionType;
	}

	public void setPaymentTransactionType(
			PaymentTransactionType paymentTransactionType) {
		this.paymentTransactionType = paymentTransactionType;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


	public Map<String, Object> getAdditionalFields() {
		return additionalFields;
	}

	public void setAdditionalFields(Map<String, Object> additionalFields) {
		this.additionalFields = additionalFields;
	}

	public boolean isCompleteCheckoutOnCallback() {
		return completeCheckoutOnCallback;
	}

	public void setCompleteCheckoutOnCallback(boolean completeCheckoutOnCallback) {
		this.completeCheckoutOnCallback = completeCheckoutOnCallback;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getProductTotalAmount() {
		return productTotalAmount;
	}

	public void setProductTotalAmount(String productTotalAmount) {
		this.productTotalAmount = productTotalAmount;
	}
    
    
}
