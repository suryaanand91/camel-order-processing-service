package com.samples.order.processing.order.processors;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bson.Document;

/**
 * The Recieved input from read queue will be transformed here and a mongo doc will be generated here
 * _id of the Mongo document will be transactionsystem:orderId
 * 
 * The new document will be saved to exchange body and exchange property (to retrieve it later).
 * Once data in body is written to mongo, it will be removed from exchange body.
 * 
 * @author suryaanand
 *
 */
public class OrderProcessingProcessor implements Processor {

	@Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> originalOrder = exchange.getIn().getBody(Map.class);
        String transactionSystem = (String) originalOrder.get("transactionsystem");
        String orderId = (String) originalOrder.get("orderId");
        String mongoId = transactionSystem + ":" + orderId;
        Document transformedOrder = new Document();
        originalOrder.put("_id", mongoId);
        exchange.setProperty("ops-transformed-order",originalOrder);
        exchange.getIn().setBody(originalOrder);

    }
}
