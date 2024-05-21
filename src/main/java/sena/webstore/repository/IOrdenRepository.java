package sena.webstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sena.webstore.model.Orden;
import sena.webstore.model.Usuario;

@Repository
public interface IOrdenRepository extends JpaRepository<Orden, Integer>{
    List<Orden> findByUsuario (Usuario usuario);
}
