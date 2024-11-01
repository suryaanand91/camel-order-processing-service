package com.samples.order.processing.order.routes;

import org.apache.camel.builder.RouteBuilder;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.samples.order.processing.order.processors.DispatchMessageProcessor;
import com.samples.order.processing.order.processors.OrderProcessingProcessor;

@Component
public class OrderProcessingRoute extends RouteBuilder {

    @Value("${app.queue.order.processing}")
    private String orderProcessingQueue;

    @Value("${app.queue.order.dispatch}")
    private String orderDispatchQueue;

    @Value("${mongodb.collection}")
    private String mongoCollection;
    
    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Override
    public void configure() {

        // Error handling (optional)
        onException(Exception.class)
            .log("Error processing message: ${exception.message}")
            .handled(true);

        // Route to listen to ActiveMQ queue, process, save to MongoDB, and forward to another queue
        from("jms:queue:" + orderProcessingQueue)
            .routeId("order-processing-main-route")
           // .log("Received order: ${body}")
            .choice()
            	.when(simple("${body[type]} == 'order'"))
            	.process(new OrderProcessingProcessor())
            	.toD("mongodb:myDb?database=" + mongoDatabase + "&collection=" + mongoCollection + "&operation=save")
            	.process(new DispatchMessageProcessor())  
            	.to("jms:queue:" + orderDispatchQueue)
            	.log("Order forwarded to dispatch queue: " + orderDispatchQueue)
            .otherwise()
            .log("Message type is not 'order'. Ignoring message.")
        .end();
        
    
    }
}
