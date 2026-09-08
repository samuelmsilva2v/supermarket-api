package com.example.demo.infrastructure.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

	@Value("${app.rabbitmq.exchange}")
	private String exchange;

	@Value("${app.rabbitmq.fila.redefinicao-senha}")
	private String filaRedefinicaoSenha;

	@Value("${app.rabbitmq.routing-key.redefinicao-senha}")
	private String routingKeyRedefinicaoSenha;

	@Bean
	public DirectExchange supermarketExchange() {
		return new DirectExchange(exchange);
	}

	@Bean
	public Queue filaRedefinicaoSenha() {
		return new Queue(filaRedefinicaoSenha, true);
	}

	@Bean
	public Binding bindingRedefinicaoSenha(Queue filaRedefinicaoSenha, DirectExchange supermarketExchange) {
		return BindingBuilder.bind(filaRedefinicaoSenha).to(supermarketExchange).with(routingKeyRedefinicaoSenha);
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
		var template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(converter);
		return template;
	}
}
