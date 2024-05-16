package sena.webstore.service;

import java.util.Optional;


import sena.webstore.model.Usuario;

public interface IUsuarioService {
    Optional<Usuario> findById(Integer id);
}
