package br.com.sisfie.teste;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class TestInstanciaSpring extends TestCase {

	public void test1() throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:applicationContext-teste.xml");
		JavaMailSender javaMailSender = (JavaMailSender) context
				.getBean("mailSender");

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("sisfie.esaf@fazenda.gov.br");
		message.setTo("igor.galvao1@gmail.com");
		message.setSubject("Sistema SISFIE - Informação sobre senha do usuário ");

		javaMailSender.send(message);

	}
}
