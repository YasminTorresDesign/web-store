package sena.webstore.service;

import java.util.List;

import sena.webstore.model.Orden;

public interface IOrdenService {
    List<Orden> findAll();
    //Optional<Orden> findById(Integer id);
    Orden save (Orden orden);
    String generarNumeroOrden();
    //List<Orden> findByUsuario (Usuario usuario);

}
