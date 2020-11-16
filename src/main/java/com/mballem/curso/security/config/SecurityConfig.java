package com.mballem.curso.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.service.UsuarioService;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
	private static final String MEDICO = PerfilTipo.MEDICO.getDesc();
	private static final String PACIENTE = PerfilTipo.PACIENTE.getDesc();
	
	@Autowired
	private UsuarioService service;
	
	//solicitações http
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//inicia as autorizações por requisição
		http.authorizeRequests()
		
		//Acesso públicos
		.antMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
		.antMatchers("/").permitAll()//aquela uri não precisa de autenticação
		.antMatchers("/u/novo/cadastro", "/u/cadastro/realizado", "/u/cadastro/paciente/salvar").permitAll()
		.antMatchers("/u/confirmacao/cadastro").permitAll()
		.antMatchers("/u/p/**").permitAll()
		
		//acessos privados admin
		.antMatchers("/u/editar/senha", "/u/confirmar/senha").hasAnyAuthority(MEDICO, PACIENTE)
		.antMatchers("/u/**").hasAuthority(ADMIN)
		
		//acessos privados medico
		.antMatchers("/medicos/dados", "/medicos/salvar", "/medicos/editar").hasAnyAuthority(MEDICO, ADMIN) //dando acesso a mais de 1 usuario
		.antMatchers("/medicos/especialidade/titulo/*").hasAnyAuthority(MEDICO, PACIENTE)
		.antMatchers("/medicos/**").hasAuthority(MEDICO)
		
		//Acesso privado para especialidades
		.antMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority(MEDICO, ADMIN)
		.antMatchers("/especialidades/titulo").hasAnyAuthority(MEDICO, ADMIN, PACIENTE)
		.antMatchers("/especialidades/**").hasAnyAuthority(ADMIN)
		
		//Acesso privado para pacientes
		.antMatchers("/pacientes/**").hasAuthority(PACIENTE)
		
		
		.anyRequest().authenticated()//Esses métodos informam que qualquer solicitação a aplicação deve estar autenticada, a menos é claro, aquelas que foram liberadas como públicas.
		.and() //método para ir concatenando instruções que são de diferentes tipos
			.formLogin() //agora a instrução é para trabalhar com login
			.loginPage("/login") // qual é a pagina de login
			.defaultSuccessUrl("/home") // e qual a página que vai abrir quando tiver sucesso no login
			.failureUrl("/login-error")//caso falhe
			.permitAll() //todo usuario vai poder acessar a página de logiin e error
		.and()//concatena e a agora vem outras instruções
			.logout()// para trabalhar com logout quando for chamado
			.logoutSuccessUrl("/login") //esta página que vai ser chamada no logout
			.permitAll()
		.and()
			.exceptionHandling()
			.accessDeniedPage("/acesso-negado")
		.and().rememberMe();
		;
	}
	
	//autenticação 
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
	}
	

}
