package com.mballem.curso.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.mballem.curso.security.service.EmailService;

@SpringBootApplication
public class DemoSecurityApplication {
	
	//implements CommandLineRunner
	
	public static void main(String[] args) {
		SpringApplication.run(DemoSecurityApplication.class, args);
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
	}

	@Autowired
	JavaMailSender sender;
	
	@Autowired
	EmailService service;
	
	/*@Override
	public void run(String... args) throws Exception {
		service.enviarPedidoDeConfirmacaoDeCadastro("aglayrtonjuliao@gmail.com", "9852pol");
	}*/
}
