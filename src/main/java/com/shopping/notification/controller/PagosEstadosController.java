package com.shopping.notification.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayalait.fecha.FormatearFechas;
import com.ayalait.logguerclass.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shopping.notification.modelo.OrdenPago;
import com.shopping.notification.service.NotificationService;
import com.shopping.notification.vo.RequestObtenerOrden;
import com.shopping.notification.vo.ResponseOrdenPago;
import com.shopping.notification.vo.ResponseResultado;
import com.shopping.notification.vo.ResponseValidarPago;

@RestController
//@RequestMapping("/api/v1/notification-pagos")
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
					OrdenPago orden= response.getOrdenPago();//response.getOrdenPago();					
					orden.setState(validar.getPagoValido().getStatus());
					orden.setRedirect_url(validar.getPagoValido().getRedirect_url());
					ResponseResultado respuesta= service.actualizarOrdenPago(orden);
					Notification noti= new Notification();
					noti.setId(UUID.randomUUID().toString());
					noti.setFecha_inicio(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
					noti.setClass_id("notification-API");
					
					if(respuesta.isStatus()) {
						noti.setAccion("Actualizada orden "+ response.getOrdenPago().getOrder_id());
						noti.setResponse("Orden "+ response.getOrdenPago().getIdpago() + " con estado "+ response.getOrdenPago().getState() +" - actualizada con estado: "+ validar.getPagoValido().getStatus());
						//Guardo log en la base
						Logger.getLogger(PagosEstadosController.class.getName()).log(Level.INFO, (String) null, orden);
						noti.setResultado(respuesta.getResultado());
						ResponseResultado result= service.guardarLog(noti);
						if(!result.isStatus()) {
							System.err.println(result.getError().getCode() +" "+ result.getError().getMenssage());
						}

					}else {
						noti.setAccion("No Actualizada orden "+ response.getOrdenPago().getOrder_id());
						noti.setResponse("No se actualizo la Orden "+ response.getOrdenPago().getOrder_id() + " con estado "+ response.getOrdenPago().getState() +" - con el estado: "+ validar.getPagoValido().getStatus());
						noti.setResultado(respuesta.getResultado());
						noti.setFecha_fin(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
						ResponseResultado result = service.guardarLog(noti);
						if(!result.isStatus()) {
							System.err.println(result.getError().getCode() +" "+ result.getError().getMenssage());
						}

					}
					
				}else {
					/*Notification noti= new Notification();
					noti.setId(UUID.randomUUID().toString());
					noti.setFecha_inicio(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
					noti.setClass_id("notification-API");
					noti.setAccion("Actualiar orden "+response.getOrdenPago().getOrder_id());
					noti.setRequest("Estado Orden a actualizar API d-localgo "+validar.getPagoValido().getStatus());
					noti.setResponse("Estado orden a actualizar Multi-Shop "+ response.getOrdenPago().getState());
					noti.setFecha_fin(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
					noti.setResultado("No se pudo actualizar la orden de pago "+ response.getOrdenPago().getOrder_id());
					ResponseResultado result = service.guardarLog(noti);
					if(!result.isStatus()) {
						System.err.println(result.getError().getCode() +" "+ result.getError().getMenssage());
					}*/

				}
			}
		}
	}
	
	
}
