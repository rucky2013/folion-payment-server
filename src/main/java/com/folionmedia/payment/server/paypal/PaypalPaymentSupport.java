package com.folionmedia.payment.server.paypal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsItemType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

import com.folionmedia.payment.server.api.PaymentRequest;
import com.folionmedia.payment.server.api.PaymentResponse;
import com.folionmedia.payment.server.api.PaymentTransactionState;
import com.folionmedia.payment.server.api.PaymentType;
import com.folionmedia.payment.server.api.PaymentVendorType;
import com.folionmedia.payment.server.domain.PaymentTransaction;

@Component
public class PaypalPaymentSupport {
	
	protected final Logger logger = LoggerFactory.getLogger(PaypalPaymentSupport.class);
	
	@Autowired
	private PaypalConfiguration paypalConfiguration;
	
	public PaymentTransaction buildPaymentTransaction(PaymentRequest paymentRequest) {
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		
		paymentTransaction.setPaymentType(PaymentType.THIRD_PARTY_ACCOUNT.getType());
		paymentTransaction.setVendorType(PaymentVendorType.PAYPAL_EXPRESS.getType());
		
		paymentTransaction.setUid(paymentRequest.getUid());
		paymentTransaction.setCreateTime(System.currentTimeMillis());
		paymentTransaction.setUpdateTime(System.currentTimeMillis());
		paymentTransaction.setState(PaymentTransactionState.NONE.value());
		
		//product information
		paymentTransaction.setProductId(paymentRequest.getProductId());
		paymentTransaction.setProductName(paymentRequest.getProductName());
		paymentTransaction.setProductQuantity(paymentRequest.getProductQuantity());
		paymentTransaction.setProductUnitPrice(paymentRequest.getProductUnitPrice());
		paymentTransaction.setProductDescription(paymentRequest.getProductDescription());
		paymentTransaction.setProductTotalAmount(paymentRequest.getProductTotalAmount());
		
		//money information
		paymentTransaction.setRevenueAmount(paymentRequest.getTotalAmount());
		paymentTransaction.setCurrencyCode(paymentRequest.getCurrencyCode());
		
		if(paymentRequest.getTxId() != null){
			paymentTransaction.setId(paymentRequest.getTxId() );
		}else{
			paymentRequest.setTxId(paymentTransaction.getId());
		}
		return paymentTransaction;
	}
	
	public SetExpressCheckoutReq buildSetExpressCheckoutReq(PaymentRequest paymentRequest){
		// ## SetExpressCheckoutReq
		SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();

		// URL to which the buyer's browser is returned after choosing to pay
		// with PayPal. For digital goods, you must add JavaScript to this page
		// to close the in-context experience.
		// `Note:
		// PayPal recommends that the value be the final review page on which
		// the buyer confirms the order and payment or billing agreement.`
		setExpressCheckoutRequestDetails.setReturnURL(paypalConfiguration.getReturnUrl() + paymentRequest.getTxId());

		// URL to which the buyer is returned if the buyer does not approve the
		// use of PayPal to pay you. For digital goods, you must add JavaScript
		// to this page to close the in-context experience.
		// `Note:
		// PayPal recommends that the value be the original page on which the
		// buyer chose to pay with PayPal or establish a billing agreement.`
		setExpressCheckoutRequestDetails.setCancelURL(paypalConfiguration.getCancelUrl()  + paymentRequest.getTxId() );

		//Currency Code
		CurrencyCodeType currencyID = CurrencyCodeType.valueOf(paymentRequest.getCurrencyCode());
		
		// ### Payment Information
		// list of information about the payment
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();

		// information about the first payment
		PaymentDetailsType paymentDetails = new PaymentDetailsType();
		
		List<PaymentDetailsItemType> paymentDetailsItems = new ArrayList<PaymentDetailsItemType>();
		PaymentDetailsItemType paymentDetailsItem = new PaymentDetailsItemType();
		paymentDetailsItem.setName(paymentRequest.getProductName());
		BasicAmountType itemAmount = new BasicAmountType();
		itemAmount.setCurrencyID(currencyID);
		itemAmount.setValue(paymentRequest.getProductUnitPrice());
		paymentDetailsItem.setAmount(itemAmount);
		paymentDetailsItem.setDescription(paymentRequest.getProductDescription());
		paymentDetailsItem.setQuantity(paymentRequest.getProductQuantity());
		
		paymentDetailsItems.add(paymentDetailsItem);
		paymentDetails.setPaymentDetailsItem(paymentDetailsItems);
		// Total cost of the transaction to the buyer. If shipping cost and tax
		// charges are known, include them in this value. If not, this value
		// should be the current sub-total of the order.
		//
		// If the transaction includes one or more one-time purchases, this
		// field must be equal to
		// the sum of the purchases. Set this field to 0 if the transaction does
		// not include a one-time purchase such as when you set up a billing
		// agreement for a recurring payment that is not immediately charged.
		// When the field is set to 0, purchase-specific fields are ignored.
		// 
		// * `Currency Code` - You must set the currencyID attribute to one of
		// the
		// 3-character currency codes for any of the supported PayPal
		// currencies.
		// * `Amount`
		BasicAmountType itemTotal = new BasicAmountType();
		itemTotal.setCurrencyID(currencyID);
		itemTotal.setValue(paymentRequest.getProductTotalAmount());
		paymentDetails.setItemTotal(itemTotal);
		
		BasicAmountType total = new BasicAmountType();
		total.setCurrencyID(currencyID);
		total.setValue(paymentRequest.getTotalAmount());
		paymentDetails.setOrderTotal(total);
		paymentDetails.setOrderDescription(paymentRequest.getProductDescription());

		// How you want to obtain payment. When implementing parallel payments,
		// this field is required and must be set to `Order`. When implementing
		// digital goods, this field is required and must be set to `Sale`. If
		// the
		// transaction does not include a one-time purchase, this field is
		// ignored. It is one of the following values:
		// 
		// * `Sale` - This is a final sale for which you are requesting payment
		// (default).
		// * `Authorization` - This payment is a basic authorization subject to
		// settlement with PayPal Authorization and Capture.
		// * `Order` - This payment is an order authorization subject to
		// settlement with PayPal Authorization and Capture.
		// `Note:
		// You cannot set this field to Sale in SetExpressCheckout request and
		// then change the value to Authorization or Order in the
		// DoExpressCheckoutPayment request. If you set the field to
		// Authorization or Order in SetExpressCheckout, you may set the field
		// to Sale.`
		paymentDetails.setPaymentAction(PaymentActionCodeType.SALE);

		// Your URL for receiving Instant Payment Notification (IPN) about this
		// transaction. If you do not specify this value in the request, the
		// notification URL from your Merchant Profile is used, if one exists.
		//paymentDetails1.setNotifyURL("http://localhost/ipn");
		paymentDetails.setNotifyURL(paypalConfiguration.getNotifyUrl() + paymentRequest.getTxId());

		paymentDetailsList.add(paymentDetails);

		setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);
		setExpressCheckoutRequestDetails.setNoShipping(PaypalConstants.PAYPAL_NO_SHIPPINT_VALUE);
		SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
		SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(setExpressCheckoutRequestDetails);
		setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);
		return setExpressCheckoutReq;
	}
	
	public PaymentResponse build(PaymentRequest paymentRequest, SetExpressCheckoutResponseType setExpressCheckoutResponse){
		PaymentResponse paymentResponse = this.copyCommonValue(paymentRequest);
		// ## Accessing response parameters
		// You can access the response parameters using getter methods in
		// response object as shown below
		// ### Success values
		if (setExpressCheckoutResponse.getAck().getValue().equalsIgnoreCase("success")) {

			// ### Redirecting to PayPal for authorization
			// Once you get the "Success" response, needs to authorize the
			// transaction by making buyer to login into PayPal. For that,
			// need to construct redirect url using EC token from response.
			// For example,
			// `redirectURL="https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+setExpressCheckoutResponse.getToken();`

			// Express Checkout Token
			logger.info("Express Checkout Token:" + setExpressCheckoutResponse.getToken());
			paymentResponse.setSuccessful(true);
			paymentResponse.setValid(true);
			paymentResponse.setToken(setExpressCheckoutResponse.getToken());
			String redirectUrl = null;
			if(paymentResponse.isCompleteCheckoutOnCallback()){
				redirectUrl = paypalConfiguration.getRedirectUrl() + paymentResponse.getToken() + "&useraction=commit";
			}else{
				redirectUrl = paypalConfiguration.getRedirectUrl() + paymentResponse.getToken();
			}
			paymentResponse.responseMap(PaypalConstants.REDIRECTURL, redirectUrl);
			paymentResponse.responseMap(PaypalConstants.CORRELATIONID, setExpressCheckoutResponse.getCorrelationID());
			paymentResponse.responseMap(PaypalConstants.TIMESTAMP, setExpressCheckoutResponse.getTimestamp());
		}
		// ### Error Values
		// Access error values from error list using getter methods
		else {
			List<ErrorType> errorList = setExpressCheckoutResponse.getErrors();
			logger.error("API Error Message : " + errorList.get(0).getLongMessage());
			
			paymentResponse.setSuccessful(false);
		}
		return paymentResponse;
	}
	
	
	/**
	 * copy the same field from PaymentRequest to PaymentResponse
	 * @param  PaymentRequest
	 * @return PaymentResponse
	 */
	public PaymentResponse copyCommonValue(PaymentRequest paymentRequest){
		PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.getPaymentType(),paymentRequest.getPaymentVendorType());
		paymentResponse.setUid(paymentRequest.getUid());
		paymentResponse.setCompleteCheckoutOnCallback(paymentRequest.isCompleteCheckoutOnCallback());
		return paymentResponse;
	}
	
	/**
	 * copy the same field from PaymentRequest to PaymentResponse
	 * @param  PaymentRequest
	 * @return PaymentResponse
	 */
	public PaymentResponse copyCommonValue(PaymentTransaction paymentTransaction){
		PaymentResponse paymentResponse = new PaymentResponse(PaymentType.getInstance(paymentTransaction.getPaymentType()),
				PaymentVendorType.getInstance(paymentTransaction.getVendorType()));
		
		paymentResponse.setUid(paymentTransaction.getUid());
		paymentResponse.setTxId(paymentTransaction.getId());
		
		paymentResponse.setProductId(paymentTransaction.getProductId());
		paymentResponse.setProductName(paymentTransaction.getProductName());
		paymentResponse.setProductQuantity(paymentTransaction.getProductQuantity());
		paymentResponse.setProductUnitPrice(paymentTransaction.getProductUnitPrice());
		paymentResponse.setProductDescription(paymentTransaction.getProductDescription());
		paymentResponse.setProductTotalAmount(paymentTransaction.getProductTotalAmount());
		
		//money information
		paymentResponse.setTotalAmount(paymentTransaction.getRevenueAmount());
		paymentResponse.setCurrencyCode(paymentTransaction.getCurrencyCode());
		
		paymentResponse.setSuccessful(paymentTransaction.isSuccessful());
		
		paymentResponse.setVendorPayerId(paymentTransaction.getVendorPayerId());
		paymentResponse.setVendorTxId(paymentTransaction.getVendorTxId());
		
		return paymentResponse;
	}

	public GetTransactionDetailsReq buildGetTransactionDetailsReq(PaymentRequest paymentRequest) {
		// ## GetTransactionDetailsReq
		GetTransactionDetailsReq getTransactionDetailsReq = new GetTransactionDetailsReq();
		GetTransactionDetailsRequestType getTransactionDetailsRequest = new GetTransactionDetailsRequestType();

		// Unique identifier of a transaction.
		// `Note:
		// The details for some kinds of transactions cannot be retrieved with
		// GetTransactionDetails. You cannot obtain details of bank transfer
		// withdrawals, for example.`
		getTransactionDetailsRequest.setTransactionID(paymentRequest.getToken());
		getTransactionDetailsReq.setGetTransactionDetailsRequest(getTransactionDetailsRequest);

		return getTransactionDetailsReq; 
	}

	public DoExpressCheckoutPaymentReq buildDoExpressCheckoutPaymentReq(PaymentRequest paymentRequest) {
		// ## DoExpressCheckoutPaymentReq
		DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();

		DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();

		// The timestamped token value that was returned in the
		// `SetExpressCheckout` response and passed in the
		// `GetExpressCheckoutDetails` request.
		doExpressCheckoutPaymentRequestDetails.setToken(paymentRequest.getToken());

		// Unique paypal buyer account identification number as returned in
		// `GetExpressCheckoutDetails` Response
		doExpressCheckoutPaymentRequestDetails.setPayerID(paymentRequest.getVendorPayerId());

		// ### Payment Information
		// list of information about the payment
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();

		// information about the first payment
		PaymentDetailsType paymentDetails = new PaymentDetailsType();

		// Total cost of the transaction to the buyer. If shipping cost and tax
		// charges are known, include them in this value. If not, this value
		// should be the current sub-total of the order. 
		// 
		// If the transaction includes one or more one-time purchases, this field must be equal to
		// the sum of the purchases. Set this field to 0 if the transaction does
		// not include a one-time purchase such as when you set up a billing
		// agreement for a recurring payment that is not immediately charged.
		// When the field is set to 0, purchase-specific fields are ignored.
		// 
		// * `Currency Code` - You must set the currencyID attribute to one of the
		// 3-character currency codes for any of the supported PayPal
		// currencies.
		// * `Amount`
		CurrencyCodeType currencyID = CurrencyCodeType.valueOf(paymentRequest.getCurrencyCode());
		BasicAmountType total = new BasicAmountType(currencyID, paymentRequest.getTotalAmount().toString());
		paymentDetails.setOrderTotal(total);

		// How you want to obtain payment. When implementing parallel payments,
		// this field is required and must be set to `Order`. When implementing
		// digital goods, this field is required and must be set to `Sale`. If the
		// transaction does not include a one-time purchase, this field is
		// ignored. It is one of the following values:
		// 
		// * `Sale` - This is a final sale for which you are requesting payment
		// (default).
		// * `Authorization` - This payment is a basic authorization subject to
		// settlement with PayPal Authorization and Capture.
		// * `Order` - This payment is an order authorization subject to
		// settlement with PayPal Authorization and Capture.
		// Note:
		// You cannot set this field to Sale in SetExpressCheckout request and
		// then change the value to Authorization or Order in the
		// DoExpressCheckoutPayment request. If you set the field to
		// Authorization or Order in SetExpressCheckout, you may set the field
		// to Sale.
		paymentDetails.setPaymentAction(PaymentActionCodeType.SALE);
		
		// Your URL for receiving Instant Payment Notification (IPN) about this
		// transaction. If you do not specify this value in the request, the
		// notification URL from your Merchant Profile is used, if one exists.
		//paymentDetails1.setNotifyURL("http://localhost/ipn");
		paymentDetails.setNotifyURL(paypalConfiguration.getNotifyUrl());
		
		paymentDetailsList.add(paymentDetails);
		doExpressCheckoutPaymentRequestDetails.setPaymentDetails(paymentDetailsList);
		DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(doExpressCheckoutPaymentRequestDetails);
		doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);
		
		return doExpressCheckoutPaymentReq;
	}

	public PaymentResponse build(PaymentRequest paymentRequest, DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse) {
		
		PaymentResponse paymentResponse = new PaymentResponse(PaymentType.THIRD_PARTY_ACCOUNT, PaymentVendorType.PAYPAL_EXPRESS);
		
		// ## Accessing response parameters
		// You can access the response parameters using getter methods in
		// response object as shown below
		// ### Success values
		if (doExpressCheckoutPaymentResponse.getAck().getValue().equalsIgnoreCase("success")) {

			// Transaction identification number of the transaction that was
			// created.
			// This field is only returned after a successful transaction
			// for DoExpressCheckout has occurred.
			if (doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo() != null) {
				Iterator<PaymentInfoType> paymentInfoIterator = doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().iterator();
				while (paymentInfoIterator.hasNext()) {
					PaymentInfoType paymentInfo = paymentInfoIterator.next();
					logger.info("Transaction ID : "+ paymentInfo.getTransactionID());
				}
			}
		}
		// ### Error Values
		// Access error values from error list using getter methods
		else {
			List<ErrorType> errorList = doExpressCheckoutPaymentResponse
					.getErrors();
			logger.error("API Error Message : "
					+ errorList.get(0).getLongMessage());
		}
		
		return paymentResponse;
	}


}
