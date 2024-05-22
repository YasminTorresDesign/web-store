package sena.webstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sena.webstore.model.Producto;
import sena.webstore.service.IUsuarioService;
import sena.webstore.service.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("")
    public String home(Model model) {

        List<Producto> productos= productoService.findAll();
        model.addAttribute("productos", productos);

        return "administrador/home";
    }

    @GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";
	}
	
	// @GetMapping("/ordenes")
	// public String ordenes(Model model) {
	// 	model.addAttribute("ordenes", ordensService.findAll());
	// 	return "administrador/ordenes";
	// }
	
	// 

}
