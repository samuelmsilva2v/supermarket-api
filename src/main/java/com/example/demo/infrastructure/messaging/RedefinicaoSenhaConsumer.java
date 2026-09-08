package com.example.demo.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.infrastructure.components.EmailComponent;

@Component
public class RedefinicaoSenhaConsumer {

	@Autowired
	private EmailComponent emailComponent;

	@RabbitListener(queues = "${app.rabbitmq.fila.redefinicao-senha}")
	public void receber(RedefinicaoSenhaEvento evento) {
		emailComponent.enviarNovaSenha(evento.getEmail(), evento.getNome(), evento.getNovaSenha());
	}
}
