package com.shopping.notification.service;

import org.springframework.http.ResponseEntity;

public interface NotificaGestion {
	
	ResponseEntity<String> obtenerNotificaciones(String userId);
	ResponseEntity<String> guardarNotificacion(String notificacion);
	void sendNotifications(String userId);
}
