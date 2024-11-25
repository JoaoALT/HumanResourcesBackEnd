package com.example.demo.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    static final String EXCHANGE_NAME = "DemoExchange";
    static final String QUEUE_NAME = "employee.profile.update.events";

    public static void sendMessage(String message) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("45.55.216.20");
        factory.setPort(5672);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Declare exchange and queue
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

            // Publish the message
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println("Message sent to RabbitMQ: " + message);

        } catch (IOException | TimeoutException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
