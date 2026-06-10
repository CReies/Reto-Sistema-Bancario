package com.retotecnico.accounts.infrastructure.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
public class MessagingConfig {

    @Bean
    ConnectionFactory rabbitConnectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    Sender rabbitSender(ConnectionFactory connectionFactory) {
        return RabbitFlux.createSender(new SenderOptions().connectionFactory(connectionFactory));
    }

    @Bean
    Receiver rabbitReceiver(ConnectionFactory connectionFactory) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionFactory(connectionFactory));
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.messaging", name = "enabled", havingValue = "true", matchIfMissing = true)
    ApplicationRunner declareClientEventsTopology(
            Sender sender,
            @Value("${app.messaging.exchange}") String exchangeName,
            @Value("${app.messaging.queue}") String queueName,
            @Value("${app.messaging.routing-key}") String routingKey) {
        return args -> sender.declareExchange(ExchangeSpecification.exchange(exchangeName).type("topic").durable(true))
                .then(sender.declareQueue(QueueSpecification.queue(queueName).durable(true)))
                .then(sender.bind(BindingSpecification.binding(exchangeName, routingKey, queueName)))
                .subscribe();
    }
}
