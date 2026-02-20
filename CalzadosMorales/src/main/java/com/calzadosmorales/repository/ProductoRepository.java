package com.calzadosmorales.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.calzadosmorales.entity.Color;
import com.calzadosmorales.entity.Producto;
import com.calzadosmorales.entity.Talla;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Verificación de duplicados
    boolean existsByNombreAndTallaAndColor(String nombre, Talla talla, Color color);
    
    // Conteo para dashboard
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= 3 AND p.estado = true")
    Long contarProductosStockCritico();
    
    // CONSULTA COMPARTIDA (STOCK CON FILTROS)
    @Query(value = "CALL sp_ConsultaStockFiltros(:idCat, :talla)", nativeQuery = true)
    List<Object[]> consultaStockFiltros(@Param("idCat") int idCat, @Param("talla") String talla);

    // CONSULTA VENDEDOR #3 (Productos estancados)
    @Query(value = "CALL sp_VendedorProductosEstancados()", nativeQuery = true)
    List<Object[]> productosEstancados();
}