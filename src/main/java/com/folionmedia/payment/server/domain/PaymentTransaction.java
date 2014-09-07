package com.folionmedia.payment.server.domain;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;

import com.folionmedia.payment.server.api.PaymentTransactionState;

@Document
public class PaymentTransaction {

	@Id
	private String id;
	
	@Field
	private int state;
	
	@Field
	private long createTime;
	
	@Field
	private long updateTime;
	
	@Field
	private long finalTime;
	
	@Field
	private String uid;
	
	@Field
	private String paymentType;
	
	@Field
	private String vendorType;
	
	@Field
	private String vendorPayerId;
	
	@Field
	private String vendorPayerName = null;
	
	@Field
	private String vendorPayerCountry = null;
	
	@Field
	private String vendorTxId;
	
	@Field
	private String productId;
	
	@Field
	private String productName;
	
	@Field
	private String productDescription;
	
	@Field
	private int productQuantity = -1;
	
	@Field
	private String productUnitPrice = "0";

	@Field
	private String productTotalAmount = "0";
	
	@Field
	//netPayoutAmount=revenueAmount-vendorFeeAmount
	private String revenueAmount = "0";
	@Field
	private String netPayoutAmount = "0";
	@Field
	private String vendorFeeAmount = "0";
	
	@Field
	private String currencyCode = "USD";

	public PaymentTransaction(){
		id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public long getFinalTime() {
		return finalTime;
	}

	public void setFinalTime(long finalTime) {
		this.finalTime = finalTime;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getVendorType() {
		return vendorType;
	}

	public void setVendorType(String vendorType) {
		this.vendorType = vendorType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	
	public String getRevenueAmount() {
		return revenueAmount;
	}

	public void setRevenueAmount(String revenueAmount) {
		this.revenueAmount = revenueAmount;
	}

	public String getNetPayoutAmount() {
		return netPayoutAmount;
	}

	public void setNetPayoutAmount(String netPayoutAmount) {
		this.netPayoutAmount = netPayoutAmount;
	}

	public String getVendorFeeAmount() {
		return vendorFeeAmount;
	}

	public void setVendorFeeAmount(String vendorFeeAmount) {
		this.vendorFeeAmount = vendorFeeAmount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public boolean isSuccessful(){
		return state == PaymentTransactionState.COMPLETE.value();
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

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getVendorPayerCountry() {
		return vendorPayerCountry;
	}

	public void setVendorPayerCountry(String vendorPayerCountry) {
		this.vendorPayerCountry = vendorPayerCountry;
	}

	public String getVendorPayerName() {
		return vendorPayerName;
	}

	public void setVendorPayerName(String vendorPayerName) {
		this.vendorPayerName = vendorPayerName;
	}
	
	
}
