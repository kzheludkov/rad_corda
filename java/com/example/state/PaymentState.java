package com.example.state;

import com.example.contract.PaymentContract;
import com.example.model.Payment;
import com.example.schema.PaymentSchemaV1;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.PersistentStateRef;
import net.corda.core.schemas.QueryableState;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static net.corda.core.crypto.CryptoUtils.getKeys;

// TODO: Implement QueryableState and add ORM code (to match Kotlin example).

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
    private final PaymentContract contract = new PaymentContract();

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
    public PaymentContract getContract() {
        return contract;
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
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new PaymentSchemaV1());
    }

    @Override
    public PersistentState generateMappedObject(MappedSchema mappedSchema) {
        if (mappedSchema instanceof PaymentSchemaV1) {
            PersistentStateRef stateRef = null; // where I could get PersistentStateRef

            UUID linearId = this.linearId.getId();
            String transactionId = "";
            int outputIndex = 0;
            String payerParty = payer.getName().toString();
            String payerName = "";
            String payerAccount = payment.getPayerAccount();
            String payeeParty = payee.getName().toString();
            String payeeName = "";
            String payeeAccount = payment.getPayeeAccount();
            int paymentAmount = payment.getAmount();

            return new PaymentSchemaV1.PersistantPayment(stateRef, linearId, transactionId, outputIndex, payerParty,
                    payerName, payerAccount, payeeParty, payeeName, payeeAccount, paymentAmount);
        } else {
            throw new IllegalArgumentException(String.format("Unsupported schema: %s", mappedSchema.getName()));
        }
    }

    /**
     * This returns true if the state should be tracked by the vault of a particular node. In this case the logic is
     * simple; track this state if we are one of the involved parties.
     */
    @Override
    public boolean isRelevant(Set<? extends PublicKey> ourKeys) {
        final List<PublicKey> partyKeys = Stream.of(payer, payee)
                .flatMap(party -> getKeys(party.getOwningKey()).stream())
                .collect(toList());
        return ourKeys
                .stream()
                .anyMatch(partyKeys::contains);

    }

    @Override
    public String toString() {
        return "PaymentState{" +
                "payment=" + payment +
                ", payer=" + payer +
                ", payee=" + payee +
                ", linearId=" + linearId +
                ", contract=" + contract +
                '}';
    }
}