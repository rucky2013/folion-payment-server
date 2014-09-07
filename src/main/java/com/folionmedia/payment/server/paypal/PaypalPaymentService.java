package com.folionmedia.payment.server.paypal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentResponseDetailsType;
import urn.ebay.apis.eBLBaseComponents.GetExpressCheckoutDetailsResponseDetailsType;
import urn.ebay.apis.eBLBaseComponents.PayerInfoType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionType;

import com.folionmedia.payment.server.api.PaymentException;
import com.folionmedia.payment.server.api.PaymentRequest;
import com.folionmedia.payment.server.api.PaymentResponse;
import com.folionmedia.payment.server.api.PaymentTransactionState;
import com.folionmedia.payment.server.dao.MembershipRepository;
import com.folionmedia.payment.server.dao.PaymentTransactionRepository;
import com.folionmedia.payment.server.domain.Membership;
import com.folionmedia.payment.server.domain.PaymentTransaction;
import com.folionmedia.payment.server.service.AbstractPaymentService;
import com.folionmedia.payment.server.util.MembershipCalculator;

@Service("paypalPaymentService")
public class PaypalPaymentService extends AbstractPaymentService {
	
	protected final Logger logger = LoggerFactory.getLogger(PaypalPaymentService.class);
	
	@Autowired
	private PayPalAPIInterfaceServiceService payPalAPIInterfaceServiceService;

	@Autowired
	private PaypalPaymentSupport paypalPaymentSupport;
	
	@Autowired
	private PaymentTransactionRepository paymentTransactionRepository;
	
	@Autowired
	MembershipRepository membershipRepository;
	
	public PaymentResponse requestHostedEndpoint(PaymentRequest paymentRequest) throws PaymentException {
		
		PaymentTransaction paymentTransaction = paypalPaymentSupport.buildPaymentTransaction(paymentRequest);
		SetExpressCheckoutReq setExpressCheckoutReq = paypalPaymentSupport.buildSetExpressCheckoutReq(paymentRequest);
		SetExpressCheckoutResponseType setExpressCheckoutResponseType;
		try {
			setExpressCheckoutResponseType = payPalAPIInterfaceServiceService.setExpressCheckout(setExpressCheckoutReq);
		} catch (Exception ex) {
			throw new PaymentException(ex.getMessage());
		}
		PaymentResponse paymentResponse = paypalPaymentSupport.build(paymentRequest, setExpressCheckoutResponseType);
		if(paymentResponse.isSuccessful()){
			paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
		}
		return paymentResponse;
	}
	
	public PaymentResponse applyPaymentToTransaction(PaymentRequest paymentRequest) throws PaymentException {
		
		PaymentTransaction paymentTransaction = paymentTransactionRepository.findOne(paymentRequest.getTxId());
		
		try {
			GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
			GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType();
			getExpressCheckoutDetailsRequest.setToken(paymentRequest.getToken());
			getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);
			
			GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponseType = payPalAPIInterfaceServiceService.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
			
			logger.info("correlation id = {}", getExpressCheckoutDetailsResponseType.getCorrelationID());
			
			if(!getExpressCheckoutDetailsResponseType.getAck().getValue().equalsIgnoreCase("success")){
				throw new PaymentException("GetExpressCheckoutDetails:");
			}
			
			GetExpressCheckoutDetailsResponseDetailsType getExpressCheckoutDetailsResponseDetailsType = getExpressCheckoutDetailsResponseType.getGetExpressCheckoutDetailsResponseDetails();
			PayerInfoType payerInfoType = getExpressCheckoutDetailsResponseDetailsType.getPayerInfo();
			String payerId = payerInfoType.getPayerID();
			if(payerId == null || !payerId.equalsIgnoreCase(paymentRequest.getVendorPayerId())){
				logger.error("GetExpressCheckoutDetails: PayerId Not Matched, {}, {}", payerId, paymentRequest.getVendorPayerId());
				throw new PaymentException("GetExpressCheckoutDetails: PayerId Not Matched");
			}
			paymentTransaction.setVendorPayerId(payerId);
			
			paymentTransaction.setVendorPayerName(payerInfoType.getPayer());
			
			paymentTransaction.setVendorPayerCountry(payerInfoType.getPayerCountry().getValue());
			
			PaymentDetailsType paymentDetailsType = getExpressCheckoutDetailsResponseDetailsType.getPaymentDetails().get(0);
			if(paymentDetailsType == null){
				logger.error("GetExpressCheckoutDetails: No PaymentDetailsType Found");
				throw new PaymentException("GetExpressCheckoutDetails: No PaymentDetailsType Found");
			}
			
			DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();
			doExpressCheckoutPaymentRequestDetails.setToken(paymentRequest.getToken());
			doExpressCheckoutPaymentRequestDetails.setPayerID(paymentRequest.getVendorPayerId());
			
			doExpressCheckoutPaymentRequestDetails.setPaymentDetails(getExpressCheckoutDetailsResponseDetailsType.getPaymentDetails());
			
			DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(doExpressCheckoutPaymentRequestDetails);
			DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
			doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);
			DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponseType = payPalAPIInterfaceServiceService.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
			
			if (doExpressCheckoutPaymentResponseType.getAck().getValue().equalsIgnoreCase("success")) {
				paymentTransaction.setState(PaymentTransactionState.COMPLETE.value());
			}else{
				paymentTransaction.setState(PaymentTransactionState.FAILED.value());
			}
			DoExpressCheckoutPaymentResponseDetailsType doExpressCheckoutPaymentResponseDetailsType = doExpressCheckoutPaymentResponseType.getDoExpressCheckoutPaymentResponseDetails();
			
			PaymentInfoType paymentInfoType = doExpressCheckoutPaymentResponseDetailsType.getPaymentInfo().get(0);
			
			if(paymentInfoType.getTransactionID() != null){
				paymentTransaction.setVendorTxId(paymentInfoType.getTransactionID());
			}
			
			if(paymentInfoType.getFeeAmount() != null){
				paymentTransaction.setVendorFeeAmount(paymentInfoType.getFeeAmount().getValue());
			}
			if(paymentInfoType.getGrossAmount() != null){
				paymentTransaction.setRevenueAmount(paymentInfoType.getGrossAmount().getValue());
			}
			if(paymentInfoType.getSettleAmount() != null){
				paymentTransaction.setNetPayoutAmount(paymentInfoType.getSettleAmount().getValue());
			}else{
				double netPayoutAmount = Double.parseDouble(paymentTransaction.getRevenueAmount()) - Double.parseDouble(paymentTransaction.getVendorFeeAmount());
				paymentTransaction.setNetPayoutAmount(Double.valueOf(netPayoutAmount).toString());;
			}
			
		} catch (Exception ex) {
			throw new PaymentException(ex.getMessage());
		}
		
		paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
		
		if(paymentTransaction.isSuccessful()){
			Membership membership = new Membership();
			membership.setProductId(paymentTransaction.getProductId());
			membership.setProductQuantity(paymentTransaction.getProductQuantity());
			membership.setTxId(paymentTransaction.getId());
			membership.setExpiredTime(MembershipCalculator.calculateExpiredTime(membership.getProductId(), 
			membership.getProductQuantity()));
			membershipRepository.save(membership);
		}
		
		PaymentResponse paymentResponse = this.paypalPaymentSupport.copyCommonValue(paymentTransaction);
		return paymentResponse;
	}
	

	@Override
	public PaymentResponse cancelPaymentToTransaction(PaymentRequest paymentRequest) throws PaymentException {
		PaymentTransaction paymentTransaction = paymentTransactionRepository.findOne(paymentRequest.getTxId());
		if(!paymentTransaction.getVendorTxId().equals(paymentRequest.getToken())){
			throw new PaymentException("Express Checkout Token Not Matched!");
		}
		paymentTransaction.setState(PaymentTransactionState.CANCELLED.value());
		paymentTransactionRepository.save(paymentTransaction);
		PaymentResponse paymentResponse = this.paypalPaymentSupport.copyCommonValue(paymentTransaction);
		paymentResponse.setSuccessful(false);
		return paymentResponse;
	}
	
	public PaymentResponse authorizeAndCapture(PaymentRequest paymentRequest) throws PaymentException {
		
		PaymentTransaction paymentTransaction = paymentTransactionRepository.findOne(paymentRequest.getTxId());
		
		GetTransactionDetailsReq getTransactionDetailsReq = paypalPaymentSupport.buildGetTransactionDetailsReq(paymentRequest);
		
		GetTransactionDetailsResponseType getTransactionDetailsResponseType;
		
		try {
			getTransactionDetailsResponseType = payPalAPIInterfaceServiceService.getTransactionDetails(getTransactionDetailsReq);
		} catch (Exception ex) {
			throw new PaymentException(ex.getMessage());
		}
		
		PaymentTransactionType paymentTransactionType = getTransactionDetailsResponseType.getPaymentTransactionDetails();
		
		paymentTransaction.setVendorPayerId(paymentTransactionType.getPayerInfo().getPayerID());
		paymentTransaction.setVendorPayerCountry(paymentTransactionType.getPayerInfo().getPayerCountry().name());
		paymentTransaction.setVendorPayerName(paymentTransactionType.getPayerInfo().getPayer());
		paymentTransaction.setVendorTxId(paymentTransactionType.getPaymentInfo().getTransactionID());
		
		DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = paypalPaymentSupport.buildDoExpressCheckoutPaymentReq(paymentRequest);
		
		DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse;
		
		try {
			doExpressCheckoutPaymentResponse = payPalAPIInterfaceServiceService.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
		} catch (Exception ex) {
			throw new PaymentException(ex.getMessage());
		}
		
		if (doExpressCheckoutPaymentResponse.getAck().getValue().equalsIgnoreCase("success")) {
			paymentTransaction.setState(PaymentTransactionState.COMPLETE.value());
		}else{
			paymentTransaction.setState(PaymentTransactionState.FAILED.value());
		}
		
		paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
		
		return paypalPaymentSupport.build(paymentRequest, doExpressCheckoutPaymentResponse);
	}
	
}
