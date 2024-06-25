package com.shopping.notification.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ayalait.notificaciones.NotificacionesHTML;
import com.ayalait.utils.ErrorState;
import com.ayalait.utils.ResponsePrefactura;
import com.google.gson.Gson;
import com.shopping.notification.NotificacionesWebSocketHandler;
import com.shopping.notification.modelo.Notificaciones;
import com.shopping.notification.repositorio.NotificacionesJPA;

@Service
public class NotificaGestionImpl implements NotificaGestion {

	ErrorState error= new ErrorState();

	@Autowired
	NotificacionesJPA daoNoti;
	
	@Autowired
	 NotificacionesWebSocketHandler webSocketHandler;
	
	@Override
	public ResponseEntity<String> obtenerNotificaciones(String userId) {
		try {
			Iterator<Object> lst = daoNoti.findByUseridHTML(userId).iterator();
			List<NotificacionesHTML> lstPrefact= new ArrayList<NotificacionesHTML>();
			if(lst != null && lst.hasNext()) {
				while( lst.hasNext()) {
					Object[] objArray = (Object[]) lst.next();
					NotificacionesHTML response= new NotificacionesHTML();
					   response.setCantidad(Integer.parseInt(objArray[0].toString()));
					   response.setId(Integer.parseInt(objArray[1].toString()));
					   response.setTipo(objArray[2].toString());
					   response.setNotificacion(objArray[3].toString());
					   response.setUserid(objArray[4].toString());
					   response.setEstado(objArray[5].toString());
					   response.setClase(objArray[6].toString());
					   response.setFecha(objArray[7].toString());					  
					   response.setError(objArray[8].toString());
					   
					   lstPrefact.add(response);
				}
			}
			
			
			
			//List<Notificaciones> lstNoti=daoNoti.findByUserid(userId);
			if(!lstPrefact.isEmpty()) {
				return new ResponseEntity<String>(new Gson().toJson(lstPrefact), HttpStatus.OK);
			}else {
				error.setCode(9000);
				error.setMenssage("No existen notificaciones");
				return new ResponseEntity<String>(new Gson().toJson(error), HttpStatus.BAD_REQUEST);
			}
			
		} catch (Exception e) {
			error.setCode(9010);
			error.setMenssage(e.getCause().getMessage());
			return new ResponseEntity<String>(new Gson().toJson(error), HttpStatus.NOT_ACCEPTABLE);

		}
	}

	@Override
	public ResponseEntity<String> guardarNotificacion(String notificacion) {
		try {
			Notificaciones request = new Gson().fromJson(notificacion, Notificaciones.class);
			
				return new ResponseEntity<String>(new Gson().toJson(daoNoti.save(request)) ,HttpStatus.OK);

			
			
		} catch (Exception e) {
			error.setCode(90020);
			error.setMenssage(e.getCause().getMessage());
			return new ResponseEntity<String>(new Gson().toJson(error),HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	
	public void sendNotifications(String userId) {
        try {
    		//List<Notificaciones> lstNoti=daoNoti.findByUserid(userId);

        	Iterator<Object> lst = daoNoti.findByUseridHTML(userId).iterator();
			List<NotificacionesHTML> lstPrefact= new ArrayList<NotificacionesHTML>();
			if(lst != null && lst.hasNext()) {
				while( lst.hasNext()) {
					Object[] objArray = (Object[]) lst.next();
					NotificacionesHTML response= new NotificacionesHTML();
					   response.setCantidad(Integer.parseInt(objArray[0].toString()));
					   response.setId(Integer.parseInt(objArray[1].toString()));
					   response.setTipo(objArray[2].toString());
					   response.setNotificacion(objArray[3].toString());
					   response.setUserid(objArray[4].toString());
					   response.setEstado(objArray[5].toString());
					   response.setClase(objArray[6].toString());
					   response.setFecha(objArray[7].toString());					  
					   response.setError(objArray[8].toString());
					   
					   lstPrefact.add(response);
				}
        	
			}
        	
            webSocketHandler.notifyClients(lstPrefact);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
