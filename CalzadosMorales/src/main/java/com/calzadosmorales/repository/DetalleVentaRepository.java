package com.calzadosmorales.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.calzadosmorales.entity.DetalleVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta,Integer>{

    // PARES VENDIDOS
    @Query("""
        SELECT COALESCE(SUM(d.cantidad),0)
        FROM DetalleVenta d
        WHERE d.venta.usuario.id_usuario = ?1
        AND MONTH(d.venta.fecha)=MONTH(CURRENT_DATE)
        AND YEAR(d.venta.fecha)=YEAR(CURRENT_DATE)
    """)
    Integer paresVendidosMes(int idUsuario);


    // PRODUCTO MÁS VENDIDO (TOP 1)
    @Query("""
        SELECT d.producto.nombre
        FROM DetalleVenta d
        WHERE d.venta.usuario.id_usuario = ?1
        GROUP BY d.producto.nombre
        ORDER BY SUM(d.cantidad) DESC
    """)
    List<String> productoEstrellaMes(int idUsuario, Pageable pageable);
    
   // Categorías más vendidas del mes
    @Query("""
    	    SELECT p.categoria.nombre, SUM(d.cantidad)
    	    FROM DetalleVenta d
    	    JOIN d.producto p
    	    WHERE d.venta.usuario.id_usuario = ?1
    	    AND MONTH(d.venta.fecha)=MONTH(CURRENT_DATE)
    	    AND YEAR(d.venta.fecha)=YEAR(CURRENT_DATE)
    	    GROUP BY p.categoria.nombre
    	""")
    	List<Object[]> categoriasMasVendidas(int idUsuario);


}

