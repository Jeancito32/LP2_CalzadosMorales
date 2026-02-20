package com.calzadosmorales.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.calzadosmorales.entity.Producto;
import com.calzadosmorales.service.ProductoService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", service.listarProductos());
      
        model.addAttribute("categorias", service.listarCategorias());
        model.addAttribute("tallas", service.listarTallas());
        model.addAttribute("colores", service.listarColores());
        model.addAttribute("materiales", service.listarMateriales());
        
        model.addAttribute("producto", new Producto());
        return "productos";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, RedirectAttributes flash) {
        
      
        if (producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            flash.addFlashAttribute("error", "El precio debe ser mayor a 0.");
            return "redirect:/productos";
        }
        if (producto.getStock() < 0) {
            flash.addFlashAttribute("error", "El stock no puede ser negativo.");
            return "redirect:/productos";
        }

        
        if (producto.getId_producto() == null) {
          
            producto.setEstado(true); 
        } else {
          
            Producto actual = service.buscarProducto(producto.getId_producto());
            if (actual != null) {
                producto.setEstado(actual.getEstado()); 
            }
        }

        service.guardarProducto(producto);
        flash.addFlashAttribute("success", "Producto guardado correctamente.");
        return "redirect:/productos";
    }
 
     @GetMapping("/cambiarEstado/{id}/{estado}")
    public String cambiarEstado(@PathVariable("id") Integer id, @PathVariable("estado") boolean nuevoEstado, RedirectAttributes flash) {
        Producto p = service.buscarProducto(id);
        if (p != null) {
            p.setEstado(nuevoEstado);
            service.guardarProducto(p);
            flash.addFlashAttribute("info", "Estado del producto actualizado.");
        }
        return "redirect:/productos";
    }
     
  // ==========================================
     // NUEVAS RUTAS: CONSULTAS Y REPORTES
     // ==========================================

       @GetMapping("/consulta-stock")
       public String pantallaConsultaStock(
               @RequestParam(name = "idCat", defaultValue = "0") int idCat,
               @RequestParam(name = "talla", defaultValue = "") String talla,
               Model model) {
           
  
           model.addAttribute("listaCategorias", service.listarCategorias());
           

           var resultados = service.consultarStockConFiltros(idCat, talla);
           model.addAttribute("listaResultados", (resultados != null) ? resultados : new java.util.ArrayList<>());
           
           // Mantener los valores seleccionados en la vista (para que no se borren al filtrar)
           model.addAttribute("idCategoriaSeleccionada", idCat);
           model.addAttribute("tallaFiltro", talla);
           
           return "consultar_stock"; 
       }

       @GetMapping("/estancados")
       @PreAuthorize("hasRole('ROLE_2')") 
       public String verProductosEstancados(Model model) {

           model.addAttribute("listaEstancados", service.obtenerProductosEstancados());
           return "productos_estancados"; 
       }
}