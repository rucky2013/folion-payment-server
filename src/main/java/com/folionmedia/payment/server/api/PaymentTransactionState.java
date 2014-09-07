package com.folionmedia.payment.server.api;

public enum PaymentTransactionState {

	/**
	 * Non-final state. The transaction has been created but payment hasn't been
	 * promised yet.
	 */
	NONE(false, 1),

	// Non-final states. The transaction can still enter another state.
	/** Non-final state. Initial state until something interesting happens. */
	PENDING(false, 2),
	/** Non-final state. Payment was partially done. */
	PARTIAL(false, 3),

	// Final states. Once in one of these states, the transaction cannot be
	// updated (though errors can be added at any time).
	/**
	 * The transaction is complete. This state should not be used until payment
	 * is confirmed.
	 */
	COMPLETE(true, 4),
	/** This transaction did not complete due to a fatal error. */
	FAILED(true, 5),
	/** Something suspicious was detected during processing of this transaction. */
	SUSPICIOUS(true, 6),
	/** User chose to cancel transaction before payment. */
	CANCELLED(false, 7),
	/**
	 * Special transaction type used to record transactions with negative payout
	 * (refunds and reversals).
	 */
	REVERSE(true, 8);

	/** whether this payment state is a 'final' payment state */
	final boolean isFinal;
	/* id corresponding to 'lk_transaction_state' table */
	final int state;

	PaymentTransactionState(boolean isFinal, int txStateId) {
		this.isFinal = isFinal;
		this.state = txStateId;
	}
	
	public static PaymentTransactionState get(int txStateId){
		for(PaymentTransactionState state : PaymentTransactionState.values()){
			if(state.value() == txStateId){
				return state;
			}
		}
		throw new IllegalArgumentException("No Tx State Value " + txStateId);
	}
	
	public boolean isFinal(){
		return this.isFinal;
	}

	public int state() {
		return state;
	}
	
	public int value() {
		return state;
	}
}
