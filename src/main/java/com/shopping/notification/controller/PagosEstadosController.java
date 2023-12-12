package com.shopping.notification.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shopping.notification.modelo.OrdenPago;
import com.shopping.notification.service.NotificationService;
import com.shopping.notification.vo.RequestObtenerOrden;
import com.shopping.notification.vo.ResponseOrdenPago;
import com.shopping.notification.vo.ResponseResultado;
import com.shopping.notification.vo.ResponseValidarPago;

@RestController
public class PagosEstadosController {
	
	@Autowired
	NotificationService service;

	@PostMapping({ "notification-status" })
	@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public void statusOrdenPago(@RequestBody String id,HttpServletResponse responseHttp) throws IOException {
		RequestObtenerOrden payment= new RequestObtenerOrden();
		
		JsonParser jsonParser = new JsonParser();
	    JsonObject payment_id = (JsonObject) jsonParser.parse(id);
	    String value= payment_id.get("payment_id").getAsString();
	    payment.setPayment_id( payment_id.get("payment_id").getAsString());
		ResponseOrdenPago response= service.obtenerOrdenPagoId(payment);
		if(response.isStatus()) {
			ResponseValidarPago validar= service.consultarPago(value);
			if(validar.isStatus()) {
				//Actualizo el estado
				if(!validar.getPagoValido().getStatus().equalsIgnoreCase(response.getOrdenPago().getState())) {
					OrdenPago orden= response.getOrdenPago();
					orden.setState(validar.getPagoValido().getStatus());
					orden.setRedirect_url(validar.getPagoValido().getRedirect_url());
					ResponseResultado respuesta= service.actualizarOrdenPago(orden);
					if(respuesta.isStatus()) {
						//Guardo log en la base
						Logger.getLogger(PagosEstadosController.class.getName()).log(Level.INFO, (String) null, orden);

					}
				}
			}
		}
	}
	
	
}
