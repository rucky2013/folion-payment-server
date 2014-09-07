package com.folionmedia.payment.server.api;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


public class PaymentVendorType implements Serializable{
	
	private static final long serialVersionUID = -4429591787155383373L;

	private static final Map<String, PaymentVendorType> TYPES = new LinkedHashMap<String, PaymentVendorType>();

    public static final PaymentVendorType TEMPORARY  = new PaymentVendorType("Temporary", "This is a temporary Order Payment");
    
    public static final PaymentVendorType PASSTHROUGH  = new PaymentVendorType("Passthrough", "Passthrough Payment");
    
    public static final PaymentVendorType PAYPAL_EXPRESS  = new PaymentVendorType("PayPal_Express", "PayPal Express Checkout");

    public static PaymentVendorType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public PaymentVendorType() {
        // do nothing
    }

    public PaymentVendorType(String type, String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

     public String getType() {
        return type;
    }

     public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)){
            TYPES.put(type, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaymentVendorType other = (PaymentVendorType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    
    @Override
    public String toString(){
    	return this.type;
    }
}
