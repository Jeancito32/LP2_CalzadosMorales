package com.calzadosmorales.service;

import com.calzadosmorales.entity.*;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PdfService {

    public void exportarVentaPDF(HttpServletResponse response, Venta venta) {
        try {
            // 1. CARGAR DISEÑO (Asegúrate que el archivo se llame comprobante.jrxml en Eclipse)
            File file = ResourceUtils.getFile("classpath:reports/comprobante.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            // 2. PARÁMETROS DE CABECERA
            Map<String, Object> parameters = new HashMap<>();
            
            String nombreCliente = "";
            String docCliente = "";
            
            if (venta.getCliente() instanceof PersonaNatural) {
                PersonaNatural pn = (PersonaNatural) venta.getCliente();
                nombreCliente = pn.getNombre() + " " + pn.getApellido();
                docCliente = "DNI: " + pn.getDni();
            } else if (venta.getCliente() instanceof PersonaJuridica) {
                PersonaJuridica pj = (PersonaJuridica) venta.getCliente();
                nombreCliente = pj.getRazonSocial();
                docCliente = "RUC: " + pj.getRuc();
            }

            parameters.put("p_titulo", venta.getTipoComprobante().toUpperCase());
            parameters.put("p_cliente", nombreCliente);
            parameters.put("p_documento", docCliente);
            parameters.put("p_total", "S/ " + venta.getTotal().toString());

            // 3. DATOS DE LA TABLA (DETALLE)
            var detalleDS = venta.getDetalles().stream().map(d -> {
                Map<String, Object> map = new HashMap<>();
                map.put("productoNombre", d.getProducto().getNombre());
                map.put("cantidad", d.getCantidad().toString());
                map.put("precioUnitario", d.getPrecio().toString());
                map.put("subtotal", d.getSubtotal().toString());
                return map;
            }).collect(Collectors.toList());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(detalleDS);

            // 4. GENERAR PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            
            // Configurar respuesta para el navegador
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=comprobante_" + venta.getId_venta() + ".pdf");
            
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

        } catch (Exception e) {
            System.err.println("ERROR EN PDF SERVICE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}