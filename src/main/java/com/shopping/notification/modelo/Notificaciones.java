package com.shopping.notification.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the notificaciones database table.
 * 
 */
@Entity
@Table(name="notificaciones")
@NamedQuery(name="Notificaciones.findAll", query="SELECT n FROM Notificaciones n")
public class Notificaciones implements Serializable {
	private static final long serialVersionUID = 1L;

	private String estado;
	@Id
	private int id;

	private String notificacion;

	private String tipo;

	private String userid;
	private String clase;
	private String fecha;
	private String error;

	public Notificaciones() {
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNotificacion() {
		return this.notificacion;
	}

	public void setNotificacion(String notificacion) {
		this.notificacion = notificacion;
	}

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}