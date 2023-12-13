package com.shopping.notification.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

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
		Response response = null;
		Client cliente = ClientBuilder.newClient();
		String responseJson = "";
		ResponseOrdenPago responseOP = new ResponseOrdenPago();

		try {
			try {
				WebTarget webTarjet = cliente.target(this.stock+"/shopping/orden/obtener");
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
			}

			return responseOP;
		} finally {
			if (response != null) {
				response.close();
			}

			if (cliente != null) {
				cliente.close();
			}

		}
	}

	@Override
	public ResponseValidarPago consultarPago(String payId) {
		Response response = null;
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

		}
	}

	@Override
	public ResponseResultado actualizarOrdenPago(OrdenPago orden) {
		Response response = null;
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

		}
	}

	@Override
	public String guardarLog(Notification noti) {
		Response response = null;
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

		}
	}

}
