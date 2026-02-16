package com.calzadosmorales.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.calzadosmorales.entity.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer>{

    // 🔵 TOTAL VENDIDO DEL MES POR VENDEDOR
	@Query("""
			SELECT COALESCE(SUM(v.total),0)
			FROM Venta v
			WHERE v.usuario.id_usuario = ?1
			AND MONTH(v.fecha)=MONTH(CURRENT_DATE)
			AND YEAR(v.fecha)=YEAR(CURRENT_DATE)
			""")
			Double totalVentasMes(int idUsuario);



    // 🔵 CANTIDAD DE VENTAS DEL MES
	@Query("""
			SELECT COUNT(v)
			FROM Venta v
			WHERE v.usuario.id_usuario = ?1
			AND MONTH(v.fecha)=MONTH(CURRENT_DATE)
			AND YEAR(v.fecha)=YEAR(CURRENT_DATE)
			""")
			Integer cantidadVentasMes(int idUsuario);


}