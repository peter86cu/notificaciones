package com.shopping.notification.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ayalait.fecha.FormatearFechas;
import com.ayalait.logguerclass.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.shopping.notification.modelo.OrdenPago;
import com.shopping.notification.vo.ErrorState;
import com.shopping.notification.vo.RequestObtenerOrden;
import com.shopping.notification.vo.ResponseOrdenPago;
import com.shopping.notification.vo.ResponseResultado;
import com.shopping.notification.vo.ResponseValidarPago;
import com.shopping.notification.vo.ValidarPagoResponse;

@Service
public class NotificationServiceImpl implements NotificationService {

	public String stock;
	public String logger;
	private String dlogGo;
	private String auterizacion;
	ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();
	private boolean desarrollo = true;

	@Autowired
	RestTemplate restTemplate;

	void cargarServer() throws IOException {
		Properties p = new Properties();

		try {
			URL url = this.getClass().getClassLoader().getResource("application.properties");
			if (url == null) {
				throw new IllegalArgumentException("application.properties" + " is not found 1");
			} else {
				InputStream propertiesStream = url.openStream();
				p.load(propertiesStream);
				propertiesStream.close();
				this.stock = p.getProperty("server.stock");
				this.dlogGo = p.getProperty("server.dlocalgo");
				this.auterizacion = p.getProperty("server.token");
				this.logger = p.getProperty("server.logger");

			}
		} catch (FileNotFoundException var3) {
			System.err.println(var3.getMessage());
		}

	}

	public NotificationServiceImpl() {
		try {
			if (desarrollo) {
				stock = "http://localhost:8082";
				logger = "http://localhost:8086";
				dlogGo = "https://api-sbx.dlocalgo.com/v1/payments/";
				auterizacion = "ICxxdYAWmYGMxBqBHYxwvEJotcExWHUZ:qKRLqfsYE1LZHS2PvPFPjZM5XUY8HT5Aj11UHUAD";
			} else {
				cargarServer();
			}
		} catch (IOException var2) {
			System.err.println(var2.getMessage());
		}

	}

	@Override
	public ResponseOrdenPago obtenerOrdenPagoId(RequestObtenerOrden payment_id) {

		ResponseOrdenPago responseOP = new ResponseOrdenPago();
		 Notification noti= new Notification();

		try {
			HttpHeaders headers = new HttpHeaders();
			String url = this.stock + "/shopping/orden/obtener/";

			HttpEntity<RequestObtenerOrden> requestEntity = new HttpEntity<>(payment_id, headers);
			noti.setFecha_inicio(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
			noti.setClass_id("notification-API");
			noti.setRequest(ow.writeValueAsString(requestEntity));
			noti.setAccion("obtenerOrdenPagoId");	
			noti.setId(UUID.randomUUID().toString());
			ResponseEntity<OrdenPago> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					OrdenPago.class, new Object[0]);

			if (response.getStatusCodeValue() == 200) {
				
				responseOP.setStatus(true);
				responseOP.setOrdenPago(response.getBody());
				noti.setResponse(ow.writeValueAsString(responseOP));
				
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {
			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseOP.setCode(data.getCode());
			responseOP.setError(data);
			try {
				noti.setResponse(ow.writeValueAsString(responseOP));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noti.setFecha_fin(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
		ResponseResultado result= guardarLog(noti);
		if(!result.isStatus()) {
			System.err.println(result.getError().getCode() +" "+ result.getError().getMenssage());
		}

		return responseOP;

	}

	@Override
	public ResponseValidarPago consultarPago(String payId) {
		ResponseValidarPago responseOrder = new ResponseValidarPago();
		 Notification noti= new Notification();


		try {

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + auterizacion);
			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
			noti.setFecha_inicio(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
			noti.setClass_id("notification-API");
			noti.setRequest(ow.writeValueAsString(requestEntity));
			noti.setAccion("consultarPago");	
			noti.setId(UUID.randomUUID().toString());
			
			ResponseEntity<ValidarPagoResponse> response = restTemplate.exchange(this.dlogGo + payId, HttpMethod.GET,
					requestEntity, ValidarPagoResponse.class, payId);

			if (response.getStatusCodeValue() == 200) {
				responseOrder.setStatus(true);
				responseOrder.setPagoValido(response.getBody());
				noti.setResponse(ow.writeValueAsString(responseOrder));			
			}

		} catch (org.springframework.web.client.HttpServerErrorException e) {

			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseOrder.setCode(data.getCode());
			responseOrder.setError(data);
			try {
				noti.setResponse(ow.writeValueAsString(responseOrder));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noti.setFecha_fin(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
		ResponseResultado result= guardarLog(noti);
		if(!result.isStatus()) {
			System.err.println(result.getError().getCode() +" "+ result.getError().getMenssage());
		}
		return responseOrder;

	}

	@Override
	public ResponseResultado actualizarOrdenPago(OrdenPago orden) {
		ResponseResultado responseOP = new ResponseResultado();
		 Notification noti= new Notification();

		try {
			HttpHeaders headers = new HttpHeaders();
			String url = this.stock + "/shopping/orden/crear";

			HttpEntity<OrdenPago> requestEntity = new HttpEntity<>(orden, headers);
			
			noti.setFecha_inicio(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
			noti.setClass_id("notification-API");
			noti.setRequest(ow.writeValueAsString(requestEntity));
			noti.setAccion("actualizarOrdenPago");	
			noti.setId(UUID.randomUUID().toString());
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class, new Object[0]);

			if (response.getStatusCodeValue()==200) {
				responseOP.setStatus(true);
				responseOP.setResultado(response.getBody());
				noti.setResponse(ow.writeValueAsString(responseOP));			
			}
			
			
		} catch (org.springframework.web.client.HttpServerErrorException e) {

			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseOP.setCode(data.getCode());
			responseOP.setError(data);
			try {
				noti.setResponse(ow.writeValueAsString(responseOP));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noti.setFecha_fin(FormatearFechas.obtenerFechaPorFormato("yyyy-MM-dd hh:mm:ss"));
		ResponseResultado result= guardarLog(noti);
		if(!result.isStatus()) {
			System.err.println(result.getError().getCode() +" "+ result.getError().getMenssage());
		}
		
		return responseOP;

		
	}

	@Override
	public ResponseResultado guardarLog(Notification noti) {
		
		ResponseResultado responseResult = new ResponseResultado();
		try {
			HttpHeaders headers = new HttpHeaders();
			String url = this.logger + "/notification";
			
			HttpEntity<Notification> requestEntity = new HttpEntity<>(noti, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					String.class, new Object[0]);
			
			if(response.getStatusCodeValue()==201) {
				responseResult.setCode(response.getStatusCodeValue());
				responseResult.setStatus(true);
				responseResult.setResultado(response.getBody());
				return responseResult;
			}
			
		} catch (org.springframework.web.client.HttpServerErrorException e) {

			ErrorState data = new ErrorState();
			data.setCode(e.getStatusCode().value());
			data.setMenssage(e.getMessage());
			responseResult.setCode(data.getCode());
			responseResult.setError(data);
			return responseResult;

		}

		return responseResult;

		
	}

}
