package com.shopping.notification.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ayalait.logguerclass.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shopping.notification.modelo.OrdenPago;
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

	private boolean desarrollo = false;
	
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
				this.dlogGo= p.getProperty("server.dlocalgo");
				this.auterizacion= p.getProperty("server.token");
				this.logger=p.getProperty("server.logger");

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
				dlogGo="https://api-sbx.dlocalgo.com/v1/payments/";
				auterizacion="ICxxdYAWmYGMxBqBHYxwvEJotcExWHUZ:qKRLqfsYE1LZHS2PvPFPjZM5XUY8HT5Aj11UHUAD";
			} else {
				cargarServer();
			}
		} catch (IOException var2) {
			System.err.println(var2.getMessage());
		}

	}

	@Override
	public ResponseOrdenPago obtenerOrdenPagoId(RequestObtenerOrden payment_id) {
		/*Response response = null;
		Client cliente = ClientBuilder.newClient();
		String responseJson = "";*/
		ResponseOrdenPago responseOP = new ResponseOrdenPago();

		HttpHeaders headers = new HttpHeaders();
		String url = this.stock+"/shopping/orden/obtener";
		//headers.put(result, null)
		HttpEntity<RequestObtenerOrden> request = new HttpEntity<>(payment_id, headers);

		String response = restTemplate.postForObject(url, request, String.class);
		System.out.println(response);
				
				
				  
				//String value = (String) restTemplate.postForObject(uri, payment_id, String.class, new Object[0]);

				if(response!="") {
					responseOP.setStatus(true);
					OrdenPago data = (new Gson()).fromJson(response, OrdenPago.class);
					responseOP.setOrdenPago(data);
					return responseOP;
				}
				  
				  
				/*WebTarget webTarjet = cliente.target(this.stock+"/shopping/orden/obtener");
				Builder invoker = webTarjet.request(new String[] { "application/json" });
				ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

				try {
					String json = ow.writeValueAsString(payment_id);
					response = (Response) invoker.post(Entity.entity(json, "application/json"), Response.class);
					responseJson = (String) response.readEntity(String.class);
				} catch (JsonProcessingException var17) {
					Logger.getLogger(ResponseOrdenPago.class.getName()).log(Level.SEVERE, (String) null, var17);
				}

				switch (response.getStatus()) {
				case 200:
					responseOP.setStatus(true);
					responseOP.setCode(response.getStatus());
					OrdenPago data = (new Gson()).fromJson(responseJson, OrdenPago.class);
					responseOP.setOrdenPago(data);
					return responseOP;
				case 400:
					responseOP.setStatus(false);
					responseOP.setCode(response.getStatus());
					responseOP.setResultado(responseJson);
					return responseOP;
				case 404:
					responseOP.setStatus(false);
					responseOP.setCode(response.getStatus());
					responseOP.setResultado(responseJson);
					return responseOP;

				}
			} catch (JsonSyntaxException var15) {
				responseOP.setCode(406);
				responseOP.setStatus(false);
				responseOP.setResultado(var15.getMessage());
				return responseOP;
			} catch (ProcessingException var16) {
				responseOP.setCode(500);
				responseOP.setStatus(false);
				responseOP.setResultado(var16.getMessage());
				return responseOP;
			}*/

			return responseOP;
		
		
	}

	@Override
	public ResponseValidarPago consultarPago(String payId) {
		
		ResponseValidarPago responseOrder = new ResponseValidarPago();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + auterizacion);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		ResponseEntity<ValidarPagoResponse> response= restTemplate.exchange(this.dlogGo+payId, HttpMethod.GET, requestEntity, ValidarPagoResponse.class, payId);
		
		if(response!=null) {
			responseOrder.setStatus(true);
			responseOrder.setPagoValido(response.getBody());
			return responseOrder;
		}
		return responseOrder;
		/*Response response = null;
		Client cliente = ClientBuilder.newClient();
		String responseJson = "";
		ResponseValidarPago responseOrder = new ResponseValidarPago();

		try {
			WebTarget webTarjet = cliente.target(this.dlogGo + payId);
			Builder builder = webTarjet.request(new String[] { "application/json" }).header("Authorization",
					"Bearer " + auterizacion);
			builder.accept(new String[] { "application/json" });
			response = builder.get();
			if (response.getStatus() == 200) {
				responseJson = (String) response.readEntity(String.class);
				responseOrder.setStatus(true);
				responseOrder.setCode(response.getStatus());
				ValidarPagoResponse data = (ValidarPagoResponse) (new Gson()).fromJson(responseJson, ValidarPagoResponse.class);
				responseOrder.setPagoValido(data);
				return responseOrder;
			}
			responseOrder.setStatus(false);
			responseOrder.setCode(response.getStatus());
			responseJson = (String) response.readEntity(String.class);
			responseOrder.setRespuesta(responseJson);
			return responseOrder;

		} catch (JsonSyntaxException var15) {
			responseOrder.setCode(406);
			responseOrder.setStatus(false);
			responseOrder.setRespuesta(var15.getMessage());
			return responseOrder;
		} catch (ProcessingException var16) {
			responseOrder.setCode(500);
			responseOrder.setStatus(false);
			responseOrder.setRespuesta(var16.getMessage());
			return responseOrder;
		} finally {
			if (response != null) {
				response.close();
			}

			if (cliente != null) {
				cliente.close();
			}

		}*/
	}

	@Override
	public ResponseResultado actualizarOrdenPago(OrdenPago orden) {
		ResponseResultado responseOP = new ResponseResultado();
		HttpHeaders headers = new HttpHeaders();
		String url = this.stock + "/shopping/orden/crear";
		
		HttpEntity<OrdenPago> request = new HttpEntity<>(orden, headers);

		String response = restTemplate.postForObject(url, request, String.class);
		if(response!="") {
			responseOP.setStatus(true);
			responseOP.setResultado(response);
			return responseOP;
		}
		return responseOP;
		
		/*Response response = null;
		Client cliente = ClientBuilder.newClient();
		String responseJson = "";
		ResponseResultado responseOP = new ResponseResultado();

		try {
			try {
				WebTarget webTarjet = cliente.target(this.stock + "/shopping/orden/crear");
				Builder invoker = webTarjet.request(new String[] { "application/json" });
				ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

				try {
					String json = ow.writeValueAsString(orden);
					response = (Response) invoker.post(Entity.entity(json, "application/json"), Response.class);
					responseJson = (String) response.readEntity(String.class);
				} catch (JsonProcessingException var17) {
					Logger.getLogger(ResponseOrdenPago.class.getName()).log(Level.SEVERE, (String) null, var17);
				}

				switch (response.getStatus()) {
				case 200:
					responseOP.setStatus(true);
					responseOP.setCode(response.getStatus());
					responseOP.setResultado(responseJson);
					return responseOP;
				case 400:
					responseOP.setStatus(false);
					responseOP.setCode(response.getStatus());
					responseOP.setResultado(responseJson);
					return responseOP;
				case 404:
					responseOP.setStatus(false);
					responseOP.setCode(response.getStatus());
					responseOP.setResultado(responseJson);
					return responseOP;

				}
			} catch (JsonSyntaxException var15) {
				responseOP.setCode(406);
				responseOP.setStatus(false);
				responseOP.setResultado(var15.getMessage());
				return responseOP;
			} catch (ProcessingException var16) {
				responseOP.setCode(500);
				responseOP.setStatus(false);
				responseOP.setResultado(var16.getMessage());
				return responseOP;
			}

			return responseOP;
		} finally {
			if (response != null) {
				response.close();
			}

			if (cliente != null) {
				cliente.close();
			}

		}*/
	}

	@Override
	public String guardarLog(Notification noti) {
		HttpHeaders headers = new HttpHeaders();
		String url = this.logger + "/notification";
		String responseOP ="";
		HttpEntity<Notification> request = new HttpEntity<>(noti, headers);

		String response = restTemplate.postForObject(url, request, String.class);
		if(response!="") {
			return response;
		}
		return responseOP;
		
		/*Response response = null;
		Client cliente = ClientBuilder.newClient();
		String responseJson = "";
		String responseOP ="";

		try {
			try {
				WebTarget webTarjet = cliente.target(this.logger + "/notification");
				Builder invoker = webTarjet.request(new String[] { "application/json" });
				ObjectWriter ow = (new ObjectMapper()).writer().withDefaultPrettyPrinter();

				try {
					String json = ow.writeValueAsString(noti);
					response = (Response) invoker.put(Entity.entity(json, "application/json"), Response.class);
					responseJson = (String) response.readEntity(String.class);
				} catch (JsonProcessingException var17) {
					Logger.getLogger(ResponseOrdenPago.class.getName()).log(Level.SEVERE, (String) null, var17);
				}

					
					responseOP=responseJson;
				
					return responseOP;

				
			} catch (JsonSyntaxException var15) {
				System.err.println(var15);
			} catch (ProcessingException var16) {
				System.err.println(var16);
			}

			return responseOP;
		} finally {
			if (response != null) {
				response.close();
			}

			if (cliente != null) {
				cliente.close();
			}

		}*/
	}

}
