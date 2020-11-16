package com.mballem.curso.security.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.exception.AcessoNegado;
import com.mballem.curso.security.repository.UsuarioRepository;


@Service
public class UsuarioService implements UserDetailsService{
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UsuarioRepository repository;
	
	@Autowired
	private Datatables datatables;
	
	@Transactional(readOnly = true)
	public Usuario buscarPorEmail(String email) {
		return repository.findByEmail(email);
	}
	
	//classe do spring para saber se o usuario esta logado ou nao
	@Override @Transactional(readOnly = true) //usamos transactional para que ative a sessão com o banco de dados
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = buscarPorEmailEAtivo(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario "+username+" não encontrado")); //e faça funcionar o métdodo
		return new User(
				usuario.getEmail(), 
				usuario.getSenha(),
				AuthorityUtils.createAuthorityList(getStrings(usuario.getPerfis()))//pois senao aqui não funcionará
		);
	}
	
	//retorna 
	private String[] getStrings(List<Perfil> perfis) {
		String [] authorities = new String[perfis.size()];
		for(int i = 0; i < perfis.size(); i++) {
			authorities[i] = perfis.get(i).getDesc();
		}
		return authorities;
	}
	
	
	@Transactional(readOnly = true)
	public Map<String, Object> buscarTodos(HttpServletRequest request) {
		
		datatables.setRequest(request);
		datatables.setColunas(DatatablesColunas.USUARIOS);
		
		//resultado para paginação
		Page<?>  page = datatables.getSearch().isEmpty() 
				? repository.findAll(datatables.getPageable())
				: repository.findByEmailorPerfil(datatables.getSearch(), datatables.getPageable());
		return datatables.getResponse(page);
	}
	
	@Transactional(readOnly = false)
	public void salvarUsuario(Usuario usuario) {
		String crypt = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(crypt);
		repository.save(usuario);
	}
	
	@Transactional(readOnly = false)
	public Usuario findById(Long id) {
		
		return repository.findById(id).get();
	}
	
	@Transactional(readOnly = true)
	public Usuario buscarPorIdEPerfis(Long id, Long[] perfisId) {
		return repository.findByIdAndPerfis(id, perfisId)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário inexistente"));
	}
	
	@Transactional(readOnly = true)
	public static boolean isSenhaCorreta(String senhaDigitada, String senhaArmazenada) {
		return new BCryptPasswordEncoder().matches(senhaDigitada, senhaArmazenada);
	}
	
	@Transactional(readOnly = false)
	public void alterarSenha(Usuario usuario, String senha) {
		usuario.setSenha(new BCryptPasswordEncoder().encode(senha));
		repository.save(usuario);
		
	}
	
	@Transactional(readOnly = false)
	public void salvarCadastroPaciente(Usuario usuario) throws MessagingException {
		String crypt = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(crypt);
		usuario.addPerfil(PerfilTipo.PACIENTE);
		repository.save(usuario);
		
		emailDeConfirmacaoDeCadastro(usuario.getEmail());
	}
	
	@Transactional(readOnly = true)
	public Optional<Usuario> buscarPorEmailEAtivo(String email){
		return repository.findByEmailAndAtivo(email);
	}
	
	//CODIGO NO EMAIL
	public void emailDeConfirmacaoDeCadastro(String email) throws MessagingException {
		String codigo = Base64Utils.encodeToString(email.getBytes());
		emailService.enviarPedidoDeConfirmacaoDeCadastro(email, codigo);
	}
	
	//ATIVAÇÃO DO CADASTRO
	@Transactional(readOnly = false)
	public void ativarCadastro(String codigo) {
		String email = new String(Base64Utils.decodeFromString(codigo));
		Usuario usuario = buscarPorEmail(email);
		if(usuario.hasNotId()) {
			throw new AcessoNegado("Não foi possível ativar seu cadastro. Entre em contato com o suporte");
		}
		usuario.setAtivo(true);
	}
	
	//pedido pra redefinir senha
	@Transactional(readOnly = false)
	public void pedidoRedefinicaoDeSenha(String email) throws MessagingException {
		Usuario usuario = buscarPorEmailEAtivo(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario "+email+" não encontrado"));
		
		String verificador = RandomStringUtils.randomAlphanumeric(6);
		
		usuario.setCodigoVerificador(verificador);
		emailService.enviarPedidoDeRedefinicaoSenha(email, verificador);
	}
}
