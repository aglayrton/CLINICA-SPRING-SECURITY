package com.mballem.curso.security.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("u")
public class UsuarioController {
	
	
	@Autowired
	private MedicoService medicoService;
	
	@Autowired
	private UsuarioService service;
	
	
	//abrir página
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminMedicoPaciente(Usuario usuario) {
		return "usuario/cadastro";
	}
	
	//abrir lista de usuarios
	@GetMapping("/lista")
	public String listarUsuarios() {
		return "usuario/lista";
	}
	
	//listar usuarios na datatables
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {
		return ResponseEntity.ok(service.buscarTodos(request));
	}
	
	//salvar cadastro de usuarios por administrador
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		List<Perfil> perfis = usuario.getPerfis(); //recebe os perfis e joga na lista
		
		//se for maior que dois, é porque estou tentando cadastrar mais de 2 perfis e não pode
		if(perfis.size() > 2 ||
				perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L)))  ||
				perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))
		){
			attr.addFlashAttribute("falha", "Paciente não pode ser Admin e/ou Médico");	
			attr.addFlashAttribute("usuario", usuario);
		}else {
			try {
				service.salvarUsuario(usuario);
				attr.addFlashAttribute("sucesso", "Dados salvo com sucesso");
			}catch(DataIntegrityViolationException e) {//devio ao unique ele não deixa o email repetido ser cadastrado
				attr.addFlashAttribute("falha", "Cadastro não realizado, email já existente");
			}
			
		}
		
		return "redirect:/u/novo/cadastro/usuario";
	}
	
	//EDITAR AS CREDENCIAIS
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView preEditar(@PathVariable("id") Long id) {
		return new ModelAndView("usuario/cadastro", "usuario", service.findById(id));
	}
	
	
	//EDITAR SENHA
	@GetMapping("/editar/senha")
	public String editarSenha() {
		
		return "usuario/editar-senha";
	}
	
	//CONFIRMAR SENHA
		@PostMapping("/confirmar/senha")
		public String confirmarSenha(
				@RequestParam("senha1") String s1,
				@RequestParam("senha2") String s2,
				@RequestParam("senha3") String s3,
				@AuthenticationPrincipal User user,
				RedirectAttributes attr
		) {
			if(!s1.equals(s2)) {//caso as duas senhas digitadas estejam erradas
				attr.addFlashAttribute("falha" , "Senhas não conferem, tente novamente");
				return "redirect:/u/editar/senha";
			}
			
			Usuario u = service.buscarPorEmail(user.getUsername());
			if(!UsuarioService.isSenhaCorreta(s3, u.getSenha())){//caso a senha atual não seja diferente da digitada
				attr.addFlashAttribute("falha" , "Senha atual não confere, tente novamente");
				return "redirect:/u/editar/senha";
			}
			
			service.alterarSenha(u, s1);
			attr.addFlashAttribute("sucesso" , "Senha alterada com sucesso");
			
			return "redirect:/u/editar/senha";
		}
	
	//REDIRECIONAMENTO DE ACESSOS
	@GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long id, @PathVariable("perfis") Long[] perfisId) {
		
		Usuario usuario = service.buscarPorIdEPerfis(id, perfisId);
		
		if(usuario.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod())) &&
		   !usuario.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))
		){
			return new ModelAndView("usuario/cadastro", "usuario", usuario);
			
		}else if(usuario.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			
			Medico medico =  medicoService.buscarPorUsuarioId(id);
			//se o medico nao for encontra faz cadastro, senao, vai para edição
			return medico.hasNotId()
					? new ModelAndView("medico/cadastro", "medico" , new Medico(new Usuario(id)))
					: new ModelAndView("medico/cadastro", "medico" , medico);
			
		}else if(usuario.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {
				ModelAndView model = new ModelAndView("error");
				model.addObject("status", 403);
				model.addObject("error", "Area restrita");
				model.addObject("message","Os dados de pacientes são restrito.");
				return model;
		}
		
		return new ModelAndView("usuario/cadastro", "usuario", service.findById(id));
	}
	
	/*==================================CADASTRO DE PACIENTE COM EMAIL======================*/
	
	//abrir pagina de novo cadastro de paciente
	@GetMapping("/novo/cadastro")
	public String novoCadastro(Usuario usuario) {
		return "cadastrar-se";
	}
	
	//pagina de resposta de cadastro de paciente
	@GetMapping("/cadastro/realizado")
	public String cadastroRealizado() {
		return "fragments/mensagem";
	}
	
	//Recebe o form da página cadastra-se
	@PostMapping("/cadastro/paciente/salvar")
	public String salvarCadastroPaciente(Usuario usuario, BindingResult result) throws MessagingException {
		try {
			service.salvarCadastroPaciente(usuario);
		} catch (DataIntegrityViolationException e) { //dispara quando tentar inserir o usuario que ja tem no banco de dados
			result.reject("email", "Ops.. Este e-mail já existe.");
			return "cadastrar-se";
		}
		return "redirect:/u/cadastro/realizado";
	}
	
	@GetMapping("/confirmacao/cadastro")
	public String respostaConfirmacao(@RequestParam("codigo") String codigo, RedirectAttributes attr) {
		service.ativarCadastro(codigo);
		attr.addFlashAttribute("alerta", "sucesso");
		attr.addFlashAttribute("titulo", "Cadastro ativado!");
		attr.addFlashAttribute("texto", "Parabéns, seu cadastro está ativo");
		attr.addFlashAttribute("subtexto", "Siga com seu login e senha");
		
		return "redirect:/login";
	}
	
	
	//abre a pagina de peido de redefinicao de senha
	@GetMapping("/p/redefinir/senha")
	public String pedidoRedefinirSenha() {
		
		return "usuario/pedido-recuperar-senha";
	}
	
	//form de pedido de recuperar senha
	@GetMapping("/p/recuperar/senha")
	public String redefinirSenha(String email, ModelMap model) throws MessagingException {
		service.pedidoRedefinicaoDeSenha(email);
		model.addAttribute("sucesso", "Em instantes você receberá um e-mail para proseguir com a redefinição de sua senha");
		model.addAttribute("usuario", new Usuario(email));
		return "usuario/recuperar-senha";
	}
	
	//salvar a nova senha via recuperacao de senha
	@PostMapping("/p/nova/senha")
	public String confirmacaoDeRedefinicaoDeSenha(Usuario usuario, ModelMap model) {
		Usuario u = service.buscarPorEmail(usuario.getEmail());
		if(!usuario.getCodigoVerificador().equals(u.getCodigoVerificador())) {
			model.addAttribute("falha", "Código não confere");
			return "usuario/recuperar-senha";
		}
		
		u.setCodigoVerificador(null);
		service.alterarSenha(u, usuario.getSenha());
		model.addAttribute("alerta", "Sucesso");
		model.addAttribute("titulo", "Senha redefinida!");
		model.addAttribute("texto", "Você já pode logar no sistema");
		
		return "login";
	}
	
	
	
	
	
	
	
	
}
