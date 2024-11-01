package com.samples.order.processing.order.processors;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
/**
 * This processor is used to draft the message for dispatch queue.
 * Dispatch message will be in the format
 * {transactionsystem=Dallas, orderId=123456789, customerId=98765, orderStatus=new, id=Dallas:123456789}
 * 
 * The original transformed input will be taken from exchange property ,
 *  will be modified to new format  and kept back to exchange body for sending  it to write queue
 *  
 * @author suryaanand
 *
 */
@Component
public class DispatchMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Map<String, Object> originalOrder = (Map<String, Object>) exchange.getProperty("ops-transformed-order");
        String orderId = (String) originalOrder.get("orderId");
        String transactionSystem = (String) originalOrder.get("transactionsystem");
        String orderStatus = (String) originalOrder.get("orderStatus");
        String customerId = (String) ((Map<String, Object>) originalOrder.get("customer")).get("customerId");

        Map<String, Object> dispatchMessage = new HashMap<>();
        dispatchMessage.put("id", transactionSystem + ":" + orderId);
        dispatchMessage.put("orderId", orderId);
        dispatchMessage.put("transactionsystem", transactionSystem);
        dispatchMessage.put("orderStatus", orderStatus);
        dispatchMessage.put("customerId", customerId);

        exchange.getIn().setBody(dispatchMessage);
    }
}
