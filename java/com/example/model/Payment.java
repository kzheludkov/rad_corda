package com.example.model;

import net.corda.core.serialization.CordaSerializable;

/**
 * A simple class representing an Payment.
 * <p>
 * This is the data structure that the parties will reach agreement over. These data structures can be arbitrarily
 * complex. See https://github.com/corda/corda/blob/master/samples/irs-demo/src/main/kotlin/net/corda/irs/contract/IRS.kt
 * for a more complicated example.
 */
@CordaSerializable
public class Payment {
    private String payerName;
    private String payerAccount;
    private String payeeName;
    private String payeeAccount;
    private int amount;

    public String getPayerName() {
        return payerName;
    }

    public String getPayerAccount() {
        return payerAccount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public int getAmount() {
        return amount;
    }

    public Payment(String payerName, String payerAccount, String payeeName, String payeeAccount, int amount) {
        this.payerName = payerName;
        this.payerAccount = payerAccount;
        this.payeeName = payeeName;
        this.payeeAccount = payeeAccount;
        this.amount = amount;
    }

    // Dummy constructor used by the create-payment API endpoint.
    public Payment() {
    }

    @Override
    public String toString() {
        return "Payment{" +
                "payerAccount='" + payerAccount + '\'' +
                ", payeeAccount='" + payeeAccount + '\'' +
                ", amount=" + amount +
                '}';
    }
}