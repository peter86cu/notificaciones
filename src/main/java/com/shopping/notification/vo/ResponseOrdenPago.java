package com.shopping.notification.vo;

import com.shopping.notification.modelo.OrdenPago;

public class ResponseOrdenPago {
	
	private int code;
	private boolean status;
	private OrdenPago ordenPago;
	private String resultado;
	private ErrorState error;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public OrdenPago getOrdenPago() {
		return ordenPago;
	}
	public void setOrdenPago(OrdenPago ordenPago) {
		this.ordenPago = ordenPago;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public ErrorState getError() {
		return error;
	}
	public void setError(ErrorState error) {
		this.error = error;
	}
	

}
