package com.folionmedia.payment.server.util;

import java.util.Calendar;
import java.util.Date;

public class MembershipCalculator {

	public static long calculateExpiredTime(String productId, int productQuantity) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, productQuantity);
        return c.getTime().getTime();
	}
}
