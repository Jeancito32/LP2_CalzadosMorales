package com.calzadosmorales.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.calzadosmorales.repository.DetalleVentaRepository;
import com.calzadosmorales.repository.VentaRepository;


@Service
public class DashboardVendedorService {

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private DetalleVentaRepository detalleRepo;

    public Double ventasMes(int idUsuario){
        Double total = ventaRepo.totalVentasMes(idUsuario);
        return total != null ? total : 0.0;
    }

    public Double comisionMes(int idUsuario){
        Double total = ventaRepo.totalVentasMes(idUsuario);
        if(total == null) total = 0.0;
        return total * 0.05;
    }

    public Integer cantidadVentas(int idUsuario){
        Integer cantidad = ventaRepo.cantidadVentasMes(idUsuario);
        return cantidad != null ? cantidad : 0;
    }

    public Integer paresVendidos(int idUsuario){
        Integer pares = detalleRepo.paresVendidosMes(idUsuario);
        return pares != null ? pares : 0;
    }

    public String productoEstrella(int idUsuario){
        List<String> lista = detalleRepo.productoEstrellaMes(idUsuario, PageRequest.of(0,1));
        return lista.isEmpty() ? "Sin ventas" : lista.get(0);
    }

}
