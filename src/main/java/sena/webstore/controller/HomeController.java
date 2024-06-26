package sena.webstore.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sena.webstore.model.DetalleOrden;
import sena.webstore.model.Orden;
import sena.webstore.model.Producto;
import sena.webstore.model.Usuario;
import sena.webstore.service.IDetalleOrdenService;
import sena.webstore.service.IOrdenService;
import sena.webstore.service.IUsuarioService;
import sena.webstore.service.ProductoService;

@Controller
@RequestMapping("/")
public class HomeController {

    private final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IDetalleOrdenService detalleOrdenService;

    //para almacenar los detalles de la orden
    List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

    //datos de la orden
    Orden orden = new Orden();

    @GetMapping("")
    public String home(Model model, HttpSession session) {
		log.info("Sesion del usuario: {}", session.getAttribute("idusuario"));
		
		model.addAttribute("productos", productoService.findAll());
		
		//session
		model.addAttribute("sesion", session.getAttribute("idusuario"));        
		return "usuario/home";
    }

    @GetMapping("productohome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {
		log.info("Id producto enviado como parámetro {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();

		model.addAttribute("producto", producto);

		return "usuario/productohome";
	}

    @PostMapping("/cart")
    public String  addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
        DetalleOrden detalleOrden = new DetalleOrden();
        Producto producto = new Producto();
        double sumaTotal = 0;

        Optional<Producto> optionalProducto = productoService.get(id);
        log.info("Producto añadido: {}", optionalProducto.get());
        log.info("Cantidad: {}", cantidad);
        producto = optionalProducto.get();

		// Buscar si el producto ya está en el carrito
		boolean productoExistente = false;
		for (DetalleOrden detalle : detalles) {
			if (detalle.getProducto().getId().equals(producto.getId())) {
				// Si el producto ya está en el carrito, actualiza la cantidad y el total
				detalle.setCantidad(detalle.getCantidad() + cantidad);
				detalle.setTotal(detalle.getTotal() + (producto.getPrecio() * cantidad));
				productoExistente = true;
				break;
			}
		}
	
		if (!productoExistente) {
			// Si el producto no está en el carrito, añádelo como un nuevo detalle
			detalleOrden.setCantidad(cantidad);
			detalleOrden.setPrecio(producto.getPrecio());
			detalleOrden.setNombre(producto.getNombre());
			detalleOrden.setImagen(producto.getImagen());
			detalleOrden.setTotal(producto.getPrecio() * cantidad);
			detalleOrden.setProducto(producto);
	
			detalles.add(detalleOrden);
		}

        
        // detalleOrden.setCantidad(cantidad);
        // detalleOrden.setPrecio(producto.getPrecio());
        // detalleOrden.setNombre(producto.getNombre());
        // detalleOrden.setImagen(producto.getImagen());
        // detalleOrden.setTotal(producto.getPrecio()*cantidad);
        // detalleOrden.setProducto(producto);

        // //validar que el producto no se añada 2 veces
		// Integer idProducto=producto.getId();
		// boolean ingresado=detalles.stream()
        //     .anyMatch(p -> p.getProducto().getId()==idProducto);
		
		// if (!ingresado) {
		// detalles.add(detalleOrden);
		// }

		// Calcular el total de la orden
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

        return "usuario/carrito";
    }

    // quitar un producto del carrito
	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {

		// lista nueva de prodcutos
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNueva.add(detalleOrden);
			}
		}

		// poner la nueva lista con los productos restantes
		detalles = ordenesNueva;

		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

    @GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		//sesion
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "/usuario/carrito";
	}

    @GetMapping("/order")
	public String order(Model model, HttpSession session) {

		// Verificar si el usuario ha iniciado sesión
        Object usuarioId = session.getAttribute("idusuario");
        if (usuarioId == null) {
            // Redirigir a la página principal si no ha iniciado sesión
            return "redirect:/";
        }

		Usuario usuario =usuarioService.findById( Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		// Usuario usuario = usuarioService.findById(1).get();
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		
		return "usuario/resumenorden";
	}

	// guardar la orden
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session ) {
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		
		//usuario
		Usuario usuario =usuarioService.findById( Integer.parseInt(session.getAttribute("idusuario").toString())  ).get();
		// Usuario usuario = usuarioService.findById(1).get();

		orden.setUsuario(usuario);
		ordenService.save(orden);
		
		//guardar detalles
		for (DetalleOrden dt:detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		
		///limpiar lista y orden
		orden = new Orden();
		detalles.clear();
		
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre, Model model) {
		log.info("Nombre del producto: {}", nombre);
		List<Producto> productos= productoService.findAll().stream().filter( p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos", productos);		
		return "usuario/home";
	}

    

}
