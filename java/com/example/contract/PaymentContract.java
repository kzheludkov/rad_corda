package com.example.contract;

import com.example.state.PaymentState;
import net.corda.core.contracts.AuthenticatedObject;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * A implementation of a basic smart contract in Corda.
 * <p>
 * This contract enforces rules regarding the creation of a valid [PaymentState], which in turn encapsulates an [Payment].
 * <p>
 * For a new [Payment] to be issued onto the ledger, a transaction is required which takes:
 * - Zero input states.
 * - One output state: the new [Payment].
 * - An Create() command with the public keys of both the payer and the payee.
 * <p>
 * All contracts must sub-class the [Contract] interface.
 */
public class PaymentContract implements Contract {
    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        final AuthenticatedObject<Commands.Create> command = requireSingleCommand(tx.getCommands(), Commands.Create.class);
        requireThat(require -> {
            // Generic constraints around the Payment transaction.
            require.using("No inputs should be consumed when issuing an Payment.",
                    tx.getInputs().isEmpty());
            require.using("Only one output state should be created.",
                    tx.getOutputs().size() == 1);
            final PaymentState out = tx.outputsOfType(PaymentState.class).get(0);
            require.using("The payer and the payee cannot be the same entity.",
                    out.getPayer() != out.getPayee());
            require.using("All of the participants must be signers.",
                    command.getSigners().containsAll(out.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())));

            // Payment-specific constraints.
            require.using("The Payment's amount must be non-negative.",
                    out.getPayment().getAmount() > 0);

            return null;
        });
    }

    /**
     * This contract only implements one command, Create.
     */
    public interface Commands extends CommandData {
        class Create implements Commands {
        }
    }

    /**
     * This is a reference to the underlying legal contract template and associated parameters.
     */
    private final SecureHash legalContractReference = SecureHash.sha256("Payment contract template and params");

    @Override
    public final SecureHash getLegalContractReference() {
        return legalContractReference;
    }
}