package com.shopping.notification.controller;

import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.shopping.notification.NotificacionesWebSocketHandler;
import com.shopping.notification.modelo.Notificaciones;
import com.shopping.notification.service.NotificaGestion;


@RestController
@RequestMapping("/api/v1/notification")
public class NotificacionesController {
	
	@Autowired
	NotificaGestion service;
	
	 @Autowired
	 NotificacionesWebSocketHandler webSocketHandler;
	 
	@PostMapping(value="notificaciones/add",produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void crearPrefactura(@RequestBody String datos) {
		
		Notificaciones request = new Gson().fromJson(service.guardarNotificacion(datos).getBody(), Notificaciones.class);
		service.sendNotifications(request.getUserid()) ;
		
	}


	@GetMapping({ "notificaciones/by-user" })
	@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> obtenerNoti(@RequestParam("idusuario") String idusuario) throws IOException {
		return service.obtenerNotificaciones(idusuario);
	}
	
	@GetMapping("/send-notification/{userId}")
    public void sendNotifications(@PathVariable String userId) {
		service.sendNotifications(userId);
    }
	

	
	
}
