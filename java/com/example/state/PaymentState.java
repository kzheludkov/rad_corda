package com.example.state;

import com.example.model.Payment;
import com.example.schema.PaymentSchemaV1;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The state object recording Payment agreements between two parties.
 * <p>
 * A state must implement [ContractState] or one of its descendants.
 */
public class PaymentState implements LinearState, QueryableState {
    private final Payment payment;
    private final Party payer;
    private final Party payee;
    private final UniqueIdentifier linearId;

    /**
     * @param payment details of the Payment.
     * @param payer   the party issuing the Payment.
     * @param payee   the party receiving and approving the Payment.
     */
    public PaymentState(Payment payment,
                        Party payer,
                        Party payee) {
        this.payment = payment;
        this.payer = payer;
        this.payee = payee;
        this.linearId = new UniqueIdentifier();
    }

    public Payment getPayment() {
        return this.payment;
    }

    public Party getPayer() {
        return payer;
    }

    public Party getPayee() {
        return payee;
    }

    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(payer, payee);
    }

    @Override
    public PersistentState generateMappedObject(MappedSchema mappedSchema) {
        if (mappedSchema instanceof PaymentSchemaV1) {

            UUID linearId = this.linearId.getId();
            String payerParty = payer.getName().toString();
            String payerName = payment.getPayerName();
            String payerAccount = payment.getPayerAccount();
            String payeeParty = payee.getName().toString();
            String payeeName = payment.getPayeeName();
            String payeeAccount = payment.getPayeeAccount();
            int paymentAmount = payment.getAmount();

            return new PaymentSchemaV1.PersistantPayment(linearId, payerParty,
                    payerName, payerAccount, payeeParty, payeeName, payeeAccount, paymentAmount);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported schema: %s", mappedSchema.getName()));
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new PaymentSchemaV1());
    }
}