package com.calzadosmorales.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // IMPORTANTE
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.calzadosmorales.entity.Cliente;
import com.calzadosmorales.entity.PersonaJuridica;
import com.calzadosmorales.entity.DetalleVenta;
import com.calzadosmorales.entity.Producto;
import com.calzadosmorales.entity.Usuario;
import com.calzadosmorales.entity.Venta;
import com.calzadosmorales.repository.UsuarioRepository; // IMPORTANTE
import com.calzadosmorales.service.ClienteService;
import com.calzadosmorales.service.PdfService;
import com.calzadosmorales.service.ProductoService;
import com.calzadosmorales.service.VentaService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private ClienteService clienteService; 
    
    @Autowired
    private PdfService pdfService;

    @Autowired
    private UsuarioRepository usuarioRepo; // Inyectamos para buscar al usuario real

    // 1. PANTALLA PRINCIPAL
    @GetMapping("/nueva")
    public String nuevaVenta(Model model, HttpSession session) {
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("clientes", clienteService.listarTodos()); 
        
        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }

        BigDecimal total = carrito.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "nueva_venta"; 
    }

    // 2. AGREGAR AL CARRITO
    @PostMapping("/agregar")
    public String agregarProducto(
            @RequestParam("id_producto") Integer idProducto,
            @RequestParam("cantidad") Integer cantidad,
            HttpSession session,
            RedirectAttributes flash) {

        if (cantidad == null || cantidad <= 0) {
            flash.addFlashAttribute("error", "La cantidad debe ser mayor a 0.");
            return "redirect:/ventas/nueva";
        }

        Producto producto = productoService.buscarProducto(idProducto);
        if (producto.getStock() < cantidad) {
            flash.addFlashAttribute("error", "Stock insuficiente.");
            return "redirect:/ventas/nueva";
        }

        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();

        boolean existe = false;
        for (DetalleVenta det : carrito) {
            if (det.getProducto().getId_producto().equals(idProducto)) {
                det.setCantidad(det.getCantidad() + cantidad);
                det.setSubtotal(producto.getPrecio().multiply(new BigDecimal(det.getCantidad())));
                existe = true;
                break;
            }
        }

        if (!existe) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setCantidad(cantidad);
            detalle.setProducto(producto);
            detalle.setPrecio(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(new BigDecimal(cantidad)));
            carrito.add(detalle);
        }
        
        session.setAttribute("carrito", carrito);
        flash.addFlashAttribute("success", "Producto Agregado");
        return "redirect:/ventas/nueva";
    }

    // 3. QUITAR / LIMPIAR
    @GetMapping("/quitar/{index}")
    public String quitarDelCarrito(@PathVariable("index") int index, HttpSession session) {
        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito");
        if (carrito != null && index < carrito.size()) carrito.remove(index);
        return "redirect:/ventas/nueva";
    }
    
    @GetMapping("/limpiar")
    public String cancelarVenta(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/ventas/nueva";
    }

    // 4. GUARDAR VENTA (CON USUARIO DINÁMICO Y NÚMERO GENERADO)
    @PostMapping("/guardar")
    public String guardarVenta(
            @RequestParam("id_cliente") Integer idCliente, 
            HttpSession session, 
            RedirectAttributes flash,
            Authentication auth) { // <-- Authentication captura al usuario logueado

        List<DetalleVenta> carrito = (List<DetalleVenta>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            flash.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/ventas/nueva";
        }

        try {
            Venta venta = new Venta();
            venta.setFecha(LocalDateTime.now());
            
            // 1. CLIENTE Y TIPO DE COMPROBANTE
            Cliente clienteReal = clienteService.buscarPorId(idCliente);
            venta.setCliente(clienteReal);

            if (clienteReal instanceof PersonaJuridica) {
                venta.setTipoComprobante("Factura");
                venta.setSerie("F001");
            } else {
                venta.setTipoComprobante("Boleta");
                venta.setSerie("B001");
            }

            // 2. CAPTURAR USUARIO REAL DESDE LA SESIÓN
            String username = auth.getName();
            Usuario usuarioLogueado = usuarioRepo.findByUsuario(username);
            venta.setUsuario(usuarioLogueado); 
            
            // 3. CÁLCULO DE TOTALES
            BigDecimal total = carrito.stream()
                    .map(DetalleVenta::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            venta.setTotal(total);
            
            for (DetalleVenta d : carrito) {
                venta.agregarDetalle(d);
            }
            
            // 4. PRIMER GUARDADO (Para obtener el ID autonumérico)
            ventaService.registrarVenta(venta);

            // 5. ACTUALIZAR NÚMERO DE COMPROBANTE (Ej: 000015)
            venta.setNumero(String.format("%06d", venta.getId_venta()));
            ventaService.registrarVenta(venta); 
            
            session.removeAttribute("carrito");
            
            // Redirigir directamente al PDF
            return "redirect:/ventas/verPDF/" + venta.getId_venta();
            
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al procesar la venta: " + e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }
    
    // 5. GENERAR PDF
    @GetMapping("/verPDF/{id}")
    public void verPDF(@PathVariable("id") Integer idVenta, HttpServletResponse response) throws IOException {
        Venta venta = ventaService.buscarPorId(idVenta); 
        if (venta != null) {
            // Se asume que pdfService maneja el diseño según Boleta o Factura internamente
            pdfService.exportarVentaPDF(response, venta);
        }
    }
}