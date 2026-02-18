package com.calzadosmorales.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.calzadosmorales.entity.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer>{

    // ==========================================
    // QUERIES PARA VENDEDOR (ROL 2)
    // ==========================================
    @Query("""
            SELECT COALESCE(SUM(v.total),0) FROM Venta v
            WHERE v.usuario.id_usuario = ?1 AND MONTH(v.fecha)=MONTH(CURRENT_DATE) AND YEAR(v.fecha)=YEAR(CURRENT_DATE)
            """)
    Double totalVentasMes(int idUsuario);

    @Query("""
            SELECT COUNT(v) FROM Venta v
            WHERE v.usuario.id_usuario = ?1 AND MONTH(v.fecha)=MONTH(CURRENT_DATE) AND YEAR(v.fecha)=YEAR(CURRENT_DATE)
            """)
    Integer cantidadVentasMes(int idUsuario);

    // Consulta para el Gráfico Circular del Vendedor
    @Query(value = """
            SELECT c.nombre, COUNT(dv.id_detalle) as cantidad
            FROM detalle_venta dv
            JOIN productos p ON dv.id_producto = p.id_producto
            JOIN categorias c ON p.id_categoria = c.id_categoria
            JOIN venta v ON dv.id_venta = v.id_venta
            WHERE v.id_usuario = ?1 AND MONTH(v.fecha) = MONTH(NOW())
            GROUP BY c.nombre
            """, nativeQuery = true)
    List<Object[]> obtenerCategoriasTopVendedor(int idUsuario);

    // ==========================================
    // QUERIES PARA ADMINISTRADOR (ROL 1)
    // ==========================================
    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM venta WHERE fecha >= NOW() - INTERVAL 1 DAY", nativeQuery = true)
    Double obtenerIngresosDia();

    @Query(value = "SELECT COUNT(*) FROM venta WHERE fecha >= NOW() - INTERVAL 1 DAY", nativeQuery = true)
    Object obtenerRitmoVentas();

    @Query(value = "SELECT u.nombre, SUM(v.total) as totalVendido FROM venta v JOIN usuarios u ON v.id_usuario = u.id_usuario WHERE v.fecha >= NOW() - INTERVAL 1 DAY GROUP BY u.nombre ORDER BY totalVendido DESC", nativeQuery = true)
    List<Object[]> obtenerRankingVendedores();

    @Query(value = "SELECT * FROM venta ORDER BY fecha DESC LIMIT 5", nativeQuery = true)
    List<Venta> obtenerUltimasVentas();

    @Query(value = "SELECT DATE_FORMAT(fecha, '%d/%m') as dia, SUM(total) FROM venta WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY dia ORDER BY fecha ASC", nativeQuery = true)
    List<Object[]> obtenerVentasSemanales();
}