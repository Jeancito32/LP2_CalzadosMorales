package com.calzadosmorales.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.calzadosmorales.entity.DetalleVenta;
import com.calzadosmorales.entity.Producto;
import com.calzadosmorales.entity.Venta;
import com.calzadosmorales.repository.ProductoRepository;
import com.calzadosmorales.repository.VentaRepository;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // MÉTODO PARA REGISTRAR LA VENTA (CON TRANSACCIÓN)
    @Transactional 
    public void registrarVenta(Venta venta) {
        
        // 1. Asignar datos automáticos
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("REGISTRADA");

        // 2. Validar Stock y Restar
        for (DetalleVenta detalle : venta.getDetalles()) {
            
            Producto productoEnBd = productoRepository.findById(detalle.getProducto().getId_producto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (productoEnBd.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("No hay stock suficiente para: " + productoEnBd.getNombre());
            }

            // RESTAR STOCK
            int nuevoStock = productoEnBd.getStock() - detalle.getCantidad();
            productoEnBd.setStock(nuevoStock);

            productoRepository.save(productoEnBd);
            
            // Vincular detalle a venta
            detalle.setVenta(venta);
        }

        // 3. Guardar Venta
        ventaRepository.save(venta);
    }
    
    // MÉTODO PARA LISTAR TODAS LAS VENTAS
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    // ✅✅✅ AQUÍ ESTÁ EL MÉTODO QUE TE FALTABA ✅✅✅
    public Venta buscarPorId(Integer id) {
        // findById devuelve un Optional, usamos .orElse(null) para sacar el objeto real
        return ventaRepository.findById(id).orElse(null);
    }
    
    
        // ==========================================
        // METODOS PARA REPORTES Y CONSULTAS 
        // ==========================================

        // PARA EL VENDEDOR 
        
        // Listar ventas propias (Llama a sp_ListarMisVentas)
        public List<Object[]> listarMisVentas(int idUsuario) {
            return ventaRepository.listarMisVentas(idUsuario);
        }

        // Tarjeta: Total vendido hoy por vendedor (Llama a sp_TotalVendidoHoyVendedor)
        public Double totalVendidoHoyVendedor(int idUsuario) {
            Double total = ventaRepository.totalVendidoHoyVendedor(idUsuario);
            return (total != null) ? total : 0.0;
        }

        // Clientes que hace tiempo no compran (Llama a sp_VendedorClientesPorRecuperar)
        public List<Object[]> clientesPorRecuperar(int idUsuario) {
            return ventaRepository.clientesPorRecuperar(idUsuario);
        }

        // PARA EL ADMINISTRADOR 

        // Historial General de toda la tienda (Llama a sp_AdminHistorialGeneralVentas)
        public List<Object[]> obtenerHistorialGeneralAdmin() {
            return ventaRepository.adminHistorialGeneral();
        }

        // Reporte por Fechas (Filtro de calendario - Llama a sp_AdminReporteVentasPorFechas)
        public List<Object[]> obtenerReporteFechas(String inicio, String fin) {
            // Validación básica: si las fechas llegan vacías, evitamos llamar al SP
            if (inicio == null || inicio.isEmpty() || fin == null || fin.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            return ventaRepository.adminReporteFechas(inicio, fin);
        }

        // Sumatoria del monto total en el rango (Llama a sp_AdminSumatoriaVentasRango)
        public Double obtenerSumatoriaRango(String inicio, String fin) {
            if (inicio == null || inicio.isEmpty() || fin == null || fin.isEmpty()) {
                return 0.0;
            }
            Double total = ventaRepository.adminSumatoriaRango(inicio, fin);
            return (total != null) ? total : 0.0;
        }

        // Análisis de ventas por horario (Llama a sp_AdminAnalisisHorarioVentas)
        public List<Object[]> obtenerAnalisisHorario() {
            return ventaRepository.adminAnalisisHorario();
        }
    
}