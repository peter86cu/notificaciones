package com.shopping.notification.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopping.notification.modelo.*;

public interface NotificacionesJPA extends JpaRepository<Notificaciones, Integer>{

	 List<Notificaciones> findByUserid(String idUsuario);
	 
	 @Query(value="SELECT COUNT(*) as cantidad, notificaciones.* FROM notificaciones WHERE userid =:idUsuario GROUP BY id,tipo, notificacion ,userid,estado, clase", nativeQuery=true)
	 List<Object> findByUseridHTML(String idUsuario);
	
}
