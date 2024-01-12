package com.shopping.notification.service;

import com.ayalait.logguerclass.Notification;
import com.shopping.notification.modelo.OrdenPago;
import com.shopping.notification.vo.RequestObtenerOrden;
import com.shopping.notification.vo.ResponseOrdenPago;
import com.shopping.notification.vo.ResponseResultado;
import com.shopping.notification.vo.ResponseValidarPago;

public interface NotificationService {

	ResponseOrdenPago obtenerOrdenPagoId(RequestObtenerOrden payment_id);
	
	ResponseValidarPago consultarPago(String payId);
	
	ResponseResultado actualizarOrdenPago(OrdenPago orden);
	
	ResponseResultado guardarLog(Notification noti);
	
}
