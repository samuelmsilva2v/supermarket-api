package com.example.demo.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedefinicaoSenhaProducer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${app.rabbitmq.exchange}")
	private String exchange;

	@Value("${app.rabbitmq.routing-key.redefinicao-senha}")
	private String routingKey;

	public void publicar(RedefinicaoSenhaEvento evento) {
		rabbitTemplate.convertAndSend(exchange, routingKey, evento);
	}
}
