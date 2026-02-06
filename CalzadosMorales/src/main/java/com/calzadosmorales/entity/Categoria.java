package com.calzadosmorales.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias") 
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Integer id_categoria;

	@Column(nullable = false, unique = true)
	private String nombre;

	private Boolean estado;


	public Categoria() {
	}

	public Categoria(Integer id_categoria, String nombre, Boolean estado) {
		super();
		this.id_categoria = id_categoria;
		this.nombre = nombre;
		this.estado = estado;
	}


	public Integer getId_categoria() {
		return id_categoria;
	}

	public void setId_categoria(Integer id_categoria) {
		this.id_categoria = id_categoria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
}