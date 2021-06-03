package org.generation1.blogPessoal1.service;

import java.nio.charset.Charset;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;

import org.generation1.blogPessoal1.model.UserLogin;
import org.generation1.blogPessoal1.model.Usuario;
import org.generation1.blogPessoal1.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	 @Autowired
	 private UsuarioRepository repository;
	 
	 public Optional<Usuario> CadastrarUsuario(Usuario usuario) {
		 
		 if (usuario.getIdade() >= 18) {
			 usuario.setMaiorIdade(true);
		 }
		 else if (usuario.getIdade() <18) {
			 usuario.setMaiorIdade(false);
		 }
		 
		 if(repository.findByUsuario(usuario.getUsuario()).isPresent())
				return null;
		 
		 BCryptPasswordEncoder enconder = new BCryptPasswordEncoder();
		 
		 String senhaEnconder = enconder.encode(usuario.getSenha());
		 usuario.setSenha(senhaEnconder);
		 
		 return Optional.of(repository.save(usuario));
	 }
	 
	 public Optional<UserLogin> Logar (Optional<UserLogin> user){
		 BCryptPasswordEncoder enconder = new BCryptPasswordEncoder();
		 Optional<Usuario> usuario = repository.findByUsuario(user.get().getUsuario());
		 
		 if(usuario.isPresent()) {
			 if (enconder.matches(user.get().getSenha(), usuario.get().getSenha())) {
				 
				 String auth = user.get().getUsuario() + ":" + user.get().getSenha();
				 byte [] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				 String authHeader = "Basic " + new String(encodedAuth);
				 
				 user.get().setToken(authHeader);
				 user.get().setNome(usuario.get().getNome());
				 user.get().setId(usuario.get().getId());
				 user.get().setFoto(usuario.get().getFoto());
				 user.get().setTipo(usuario.get().getTipo());
				 
				 return user;
			 }
		 }
		 
		 return null;
	 }
}