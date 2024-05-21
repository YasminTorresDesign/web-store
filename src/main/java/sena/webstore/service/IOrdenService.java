package sena.webstore.service;

import java.util.List;
import java.util.Optional;

import sena.webstore.model.Orden;
import sena.webstore.model.Usuario;

public interface IOrdenService {
    List<Orden> findAll();
    Optional<Orden> findById(Integer id);
    Orden save (Orden orden);
    String generarNumeroOrden();
    List<Orden> findByUsuario (Usuario usuario);

}
