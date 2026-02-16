package com.calzadosmorales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.calzadosmorales.entity.Venta;

@Repository
public interface DashboardVentaRepository extends JpaRepository<Venta, Integer>{

    
    @Query("""
        SELECT COALESCE(SUM(v.total),0)
        FROM Venta v
        WHERE v.usuario.id = ?1
        AND MONTH(v.fecha)=MONTH(CURRENT_DATE)
        AND YEAR(v.fecha)=YEAR(CURRENT_DATE)
    """)
    Double totalVentasMes(int idUsuario);


    @Query("""
        SELECT COUNT(v)
        FROM Venta v
        WHERE v.usuario.id = ?1
        AND MONTH(v.fecha)=MONTH(CURRENT_DATE)
        AND YEAR(v.fecha)=YEAR(CURRENT_DATE)
    """)
    Integer cantidadVentasMes(int idUsuario);

}