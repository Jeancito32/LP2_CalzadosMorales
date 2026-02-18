package com.calzadosmorales.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.calzadosmorales.repository.ProductoRepository;
import com.calzadosmorales.repository.VentaRepository;

@Service
public class DashboardAdminService {
    @Autowired private VentaRepository ventaRepo;
    @Autowired private ProductoRepository productoRepo;

    public Map<String, Object> cargarPanelAdministrativo() {
        Map<String, Object> datos = new HashMap<>();
        Double ingresos = ventaRepo.obtenerIngresosDia();
        double actual = (ingresos != null) ? ingresos : 0.0;
        
        datos.put("ingresosDia", actual);
        datos.put("progresoMeta", Math.min((actual / 3000.0) * 100, 100.0));
        
        Object ritmoObj = ventaRepo.obtenerRitmoVentas();
        datos.put("cantidadVentas", (ritmoObj != null) ? ((Number) ritmoObj).longValue() : 0L);
        
        datos.put("productosEnAlerta", productoRepo.contarProductosStockCritico());
        
        List<Object[]> ranking = ventaRepo.obtenerRankingVendedores();
        if (ranking != null && !ranking.isEmpty()) {
            datos.put("nombreTopVendedor", ranking.get(0)[0]);
            datos.put("montoTopVendedor", ranking.get(0)[1]);
        } else {
            datos.put("nombreTopVendedor", "Sin ventas");
            datos.put("montoTopVendedor", 0.0);
        }

        datos.put("ultimasVentas", ventaRepo.obtenerUltimasVentas());
        datos.put("ventasSemanales", ventaRepo.obtenerVentasSemanales());
        return datos;
    }
}