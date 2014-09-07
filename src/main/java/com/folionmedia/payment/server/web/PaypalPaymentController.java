package com.folionmedia.payment.server.web;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.folionmedia.payment.server.api.PaymentException;
import com.folionmedia.payment.server.api.PaymentRequest;
import com.folionmedia.payment.server.api.PaymentResponse;
import com.folionmedia.payment.server.api.PaymentService;
import com.folionmedia.payment.server.paypal.PaypalConstants;

@Controller
@RequestMapping("/paypal-express")
public class PaypalPaymentController {

	@Autowired
	@Qualifier("paypalPaymentService")
	private PaymentService paymentService;
	
	//Get http://localhost:8080/paypal-express/checkout
    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String checkout(HttpServletRequest request)  throws PaymentException {
    	PaymentRequest paymentRequest = this.mockCreatePaymentRequest(request);
    	PaymentResponse paymentResponse = paymentService.requestHostedEndpoint(paymentRequest);
    	return "redirect:" + paymentResponse.responseMap(PaypalConstants.REDIRECTURL); 
    }
	
    //Get http://localhost:8080/paypal-express/return/e66eb8ef-b10b-4083-8c34-c738c0aac571?token=EC-23S24751T2745551E&PayerID=M35ZNMM3R72Q2
    @RequestMapping(value = "/return/{txId}", method = RequestMethod.GET)
    @ResponseBody
    public PaymentResponse returnEndpoint(HttpServletRequest request, @PathVariable String txId, @RequestParam Map<String, String> requestParams)  throws PaymentException {
    	String token = requestParams.get("token");
    	String playId = requestParams.get("PayerID");
    	
    	PaymentRequest paymentRequest = new PaymentRequest();
    	paymentRequest.setTxId(txId);
    	paymentRequest.setToken(token);
    	paymentRequest.setVendorPayerId(playId);
    	PaymentResponse paymentResponse = paymentService.applyPaymentToTransaction(paymentRequest);
        return paymentResponse;
    }

    private PaymentRequest mockCreatePaymentRequest(HttpServletRequest request){
    	PaymentRequest paymentRequest = new PaymentRequest();
    	
    	paymentRequest.setUid(UUID.randomUUID().toString());
    	paymentRequest.setProductId(UUID.randomUUID().toString());
    	paymentRequest.setProductName("Product Name");
    	paymentRequest.setProductDescription("Product Description");
    	paymentRequest.setProductQuantity(10);
    	paymentRequest.setProductUnitPrice("2");
    	paymentRequest.setProductTotalAmount("20");
    	
		paymentRequest.setTotalAmount("20");
		paymentRequest.setCurrencyCode("USD");
		paymentRequest.setCompleteCheckoutOnCallback(true);
		
		return paymentRequest;
    }
}
