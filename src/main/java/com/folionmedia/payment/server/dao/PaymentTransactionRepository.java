package com.folionmedia.payment.server.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.folionmedia.payment.server.domain.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository extends CrudRepository<PaymentTransaction, String> {

	PaymentTransaction findByVendorTxId(String vendorTxId);
}
