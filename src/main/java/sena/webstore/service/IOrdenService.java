package sena.webstore.service;

import java.util.List;

import sena.webstore.model.Orden;

public interface IOrdenService {
    List<Orden> findAll();
    Orden save (Orden orden);

}
