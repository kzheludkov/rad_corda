package com.example.client;

import com.example.state.PaymentState;
import com.google.common.net.HostAndPort;
import kotlin.Pair;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.DataFeed;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import net.corda.core.utilities.NetworkHostAndPortKt;
//import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Demonstration of using the CordaRPCClient to connect to a Corda Node and
 * steam some State data from the node.
 */
public class ExampleClientRPC {
    public static void main(String[] args) throws Exception { //ActiveMQException, InterruptedException, ExecutionException {
//        if (args.length != 1) {
//            throw new IllegalArgumentException("Usage: ExampleClientRPC <node address>");
//        }
//
//        final Logger logger = LoggerFactory.getLogger(ExampleClientRPC.class);
//        final NetworkHostAndPort nodeAddress = NetworkHostAndPortKt.parseNetworkHostAndPort(args[0]);
//        final CordaRPCClient client = new CordaRPCClient(nodeAddress, null, CordaRPCClientConfiguration.getDefault(), true);
//
//        // Can be amended in the com.example.Main file.
//        final CordaRPCOps proxy = client.start("user1", "test").getProxy();
//
//        // Grab all signed transactions and all future signed transactions.
//        final DataFeed<List<SignedTransaction>, SignedTransaction> txsAndFutureTxs =
//                proxy.verifiedTransactionsFeed();
//        final List<SignedTransaction> txs = txsAndFutureTxs.getSnapshot();
//        final Observable<SignedTransaction> futureTxs = txsAndFutureTxs.getUpdates();
//
//        // Log the 'placed' Payments and listen for new ones.
//        futureTxs.startWith(txs).toBlocking().subscribe(
//                transaction ->
//                        transaction.getTx().getOutputs().forEach(
//                                output -> {
//                                    final PaymentState paymentState = (PaymentState) output.getData();
//                                    logger.info(paymentState.getPayment().toString());
//                                })
//        );
    }
}
