package sena.webstore.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import sena.webstore.model.Usuario;

@Service
public interface IUsuarioService {
    Optional<Usuario> findById(Integer id);
}
