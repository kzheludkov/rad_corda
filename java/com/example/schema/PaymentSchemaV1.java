package com.example.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.PersistentStateRef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Created by konstzhe on 7/6/18.
 * PaymentSchemaV1
 */
public class PaymentSchemaV1 extends MappedSchema {

    private static int schemaVersion = 1;

    public PaymentSchemaV1() {
        super(PaymentSchema.class, schemaVersion, ImmutableList.of(PersistantPayment.class));
    }

    @Entity
    @Table(name = "corda_payments_state")
    public static class PersistantPayment extends PersistentState {

        @Column(name = "linear_id")
        private final UUID linearId;

        @Column(name = "transaction_id")
        private final String transactionId;
        @Column(name = "output_index")
        private final int outputIndex;

        @Column(name = "r3_payer_party")
        private final String payerParty;
        @Column(name = "r3_payer_name")
        private final String payerName;
        @Column(name = "r3_payer_account")
        private final String payerAccount;

        @Column(name = "r3_payee_party")
        private final String payeeParty;
        @Column(name = "r3_payee_name")
        private final String payeeName;
        @Column(name = "r3_payee_account")
        private final String payeeAccount;

        @Column(name = "r3_payment_amount")
        private final int paymentAmount;

        public PersistantPayment(PersistentStateRef stateRef, UUID linearId, String transactionId, int outputIndex,
                                 String payerParty, String payerName, String payerAccount, String payeeParty,
                                 String payeeName, String payeeAccount, int paymentAmount) {
            super(stateRef);
            this.linearId = linearId;
            this.transactionId = stateRef != null ? stateRef.getTxId() : transactionId;
            this.outputIndex = stateRef != null && stateRef.getIndex() != null ? stateRef.getIndex() : outputIndex;
            this.payerParty = payerParty;
            this.payerName = payerName;
            this.payerAccount = payerAccount;
            this.payeeParty = payeeParty;
            this.payeeName = payeeName;
            this.payeeAccount = payeeAccount;
            this.paymentAmount = paymentAmount;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public int getOutputIndex() {
            return outputIndex;
        }

        public String getPayerParty() {
            return payerParty;
        }

        public String getPayerName() {
            return payerName;
        }

        public String getPayerAccount() {
            return payerAccount;
        }

        public String getPayeeParty() {
            return payeeParty;
        }

        public String getPayeeName() {
            return payeeName;
        }

        public String getPayeeAccount() {
            return payeeAccount;
        }

        public int getPaymentAmount() {
            return paymentAmount;
        }
    }

}
