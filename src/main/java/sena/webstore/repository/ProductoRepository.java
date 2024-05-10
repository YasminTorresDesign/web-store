package sena.webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sena.webstore.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer>{

}
