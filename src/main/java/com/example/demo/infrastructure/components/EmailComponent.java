package com.example.demo.infrastructure.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailComponent {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${app.mail.remetente}")
	private String remetente;

	public void enviarNovaSenha(String destinatario, String nome, String novaSenha) {

		var mensagem = new SimpleMailMessage();
		mensagem.setFrom(remetente);
		mensagem.setTo(destinatario);
		mensagem.setSubject("Sua nova senha de acesso - Supermercado");
		mensagem.setText("""
				Olá, %s!

				Recebemos uma solicitação de redefinição de senha para a sua conta no sistema Supermercado.

				Sua nova senha de acesso é: %s

				Assim que fizer login novamente, recomendamos alterá-la em "Meu perfil".

				Se você não solicitou essa alteração, entre em contato com um administrador.
				""".formatted(nome, novaSenha));

		mailSender.send(mensagem);
	}
}
