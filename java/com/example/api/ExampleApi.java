package com.example.api;

import com.example.flow.ExampleFlow;
import com.example.state.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.DataFeed;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.NetworkMapCache;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import org.bouncycastle.asn1.x500.X500Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static net.corda.client.rpc.UtilsKt.notUsed;

// This API is accessible from /api/example. All paths specified below are relative to it.
@Path("example")
public class ExampleApi {
    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;
    private final List<String> serviceNames = ImmutableList.of("Notary", "Network Map Service");

    static private final Logger logger = LoggerFactory.getLogger(ExampleApi.class);

    public ExampleApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    /**
     * Returns the node's name.
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> whoami() {
        return ImmutableMap.of("me", myLegalName);
    }

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<CordaX500Name>> getPeers() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return ImmutableMap.of("peers", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));
    }

    /**
     * Displays all Payment states that exist in the node's vault.
     */
    @GET
    @Path("payments")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<PaymentState>> getPayments() {
        Vault.Page<PaymentState> vaultStates = rpcOps.vaultQuery(PaymentState.class);
        return vaultStates.getStates();
    }

    /**
     * Initiates a flow to agree an Payment between two parties.
     * <p>
     * Once the flow finishes it will have written the Payment to ledger. Both the payer and the payee will be able to
     * see it when calling /api/example/payments on their respective nodes.
     * <p>
     * This end-point takes a Party name parameter as part of the path. If the serving node can't find the other party
     * in its network map cache, it will return an HTTP bad request.
     * <p>
     * The flow is invoked asynchronously. It returns a future when the flow's call() method returns.
     */
    @PUT
    @Path("make-payment")
    public Response makePayment(
            @QueryParam("partyName") CordaX500Name partyName,
            @QueryParam("payerName") String payerName,
            @QueryParam("payerAccount") String payerAccount,
            @QueryParam("payeeName") String payeeName,
            @QueryParam("payeeAccount") String payeeAccount,
            @QueryParam("paymentAmount") int paymentAmount
    ) throws InterruptedException, ExecutionException {
        final Party otherParty = rpcOps.wellKnownPartyFromX500Name(partyName);

        if (otherParty == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Response.Status status;
        String msg;
        try {
            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(ExampleFlow.Initiator.class,
                            payerName, payerAccount,
                            payeeName, payeeAccount,
                            paymentAmount, otherParty);
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle
                    .getReturnValue()
                    .get();

            status = Response.Status.CREATED;
            msg = String.format("Transaction id %s committed to ledger.", result.getId());

        } catch (Throwable ex) {
            status = Response.Status.BAD_REQUEST;
            msg = ex.getMessage();
            logger.error(msg, ex);
        }

        return Response
                .status(status)
                .entity(msg)
                .build();
    }
}