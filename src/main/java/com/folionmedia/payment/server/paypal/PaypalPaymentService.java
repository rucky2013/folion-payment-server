package com.folionmedia.payment.server.paypal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.eBLBaseComponents.PaymentTransactionType;

import com.folionmedia.payment.server.api.PaymentException;
import com.folionmedia.payment.server.api.PaymentRequest;
import com.folionmedia.payment.server.api.PaymentResponse;
import com.folionmedia.payment.server.dao.MembershipRepository;
import com.folionmedia.payment.server.dao.PaymentTransactionRepository;
import com.folionmedia.payment.server.domain.Membership;
import com.folionmedia.payment.server.domain.PaymentTransaction;
import com.folionmedia.payment.server.service.AbstractPaymentService;
import com.folionmedia.payment.server.util.MembershipCalculator;
import com.folionmedia.payment.server.util.PaymentTransactionState;

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
			paymentTransaction.setVendorTxId(paymentResponse.getToken());
			paymentTransaction = paymentTransactionRepository.save(paymentTransaction);
		}
		return paymentResponse;
	}
	
	public PaymentResponse applyPaymentToTransaction(PaymentRequest paymentRequest) throws PaymentException {
		
		
		GetTransactionDetailsReq getTransactionDetailsReq = paypalPaymentSupport.buildGetTransactionDetailsReq(paymentRequest);
		
		GetTransactionDetailsResponseType getTransactionDetailsResponseType;
		
		try {
			getTransactionDetailsResponseType = payPalAPIInterfaceServiceService.getTransactionDetails(getTransactionDetailsReq);
		} catch (Exception ex) {
			throw new PaymentException(ex.getMessage());
		}
		
		logger.info("correlation id = {}", getTransactionDetailsResponseType.getCorrelationID());
		
		PaymentTransactionType paymentTransactionType = getTransactionDetailsResponseType.getPaymentTransactionDetails();
		
		PaymentTransaction paymentTransaction = paymentTransactionRepository.findOne(paymentRequest.getTxId());
		
		if(paymentTransactionType.getPayerInfo() != null){
			if(paymentTransactionType.getPayerInfo().getPayerCountry() != null){
				paymentTransaction.setCountry(paymentTransactionType.getPayerInfo().getPayerCountry().name());
			}
			if(paymentTransactionType.getPayerInfo().getPayerID() != null){
				paymentTransaction.setVendorPayerId(paymentTransactionType.getPayerInfo().getPayerID());
			}
		}
		if(paymentTransactionType.getBuyerEmailOptIn() != null){
			paymentTransaction.setEmail(paymentTransactionType.getBuyerEmailOptIn());
		}
		if(paymentTransactionType.getPaymentInfo().getTransactionID() != null){
			paymentTransaction.setVendorTxId(paymentTransactionType.getPaymentInfo().getTransactionID());
		}
		
		if(paymentTransactionType.getPaymentInfo().getSettleAmount() != null){
			paymentTransaction.setNetPayoutAmount(paymentTransactionType.getPaymentInfo().getSettleAmount().getValue());
		}
		if(paymentTransactionType.getPaymentInfo().getFeeAmount() != null){
			paymentTransaction.setVendorFeeAmount(paymentTransactionType.getPaymentInfo().getFeeAmount().getValue());
		}
		if(paymentTransactionType.getPaymentInfo().getGrossAmount() != null){
			paymentTransaction.setRevenueAmount(paymentTransactionType.getPaymentInfo().getGrossAmount().getValue());
		}
		
		paymentTransaction.setState(PaymentTransactionState.COMPLETE.value());
		
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
		
		paymentTransaction.setCountry(paymentTransactionType.getPayerInfo().getPayerCountry().name());
		paymentTransaction.setEmail(paymentTransactionType.getBuyerEmailOptIn());
		paymentTransaction.setVendorPayerId(paymentTransactionType.getPayerInfo().getPayerID());
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
