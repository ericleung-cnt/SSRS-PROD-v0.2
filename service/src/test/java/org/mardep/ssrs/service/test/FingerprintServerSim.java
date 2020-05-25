package org.mardep.ssrs.service.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;

//import com.sun.net.httpserver.HttpHandler;

public class FingerprintServerSim {

	final static Logger logger = LoggerFactory.getLogger(FingerprintServerSim.class);
	byte[] file  = null;
	public FingerprintServerSim() throws IOException {
		file = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("fingerprint.jpg"));
	}

	@SuppressWarnings("restriction")
	public void start() throws Exception {
		logger.info("Start server.");
		com.sun.net.httpserver.HttpServer httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(15271), 0);
		httpServer.createContext("/fpoperation", new com.sun.net.httpserver.HttpHandler() {
			public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
				try{
					file = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("fingerprint.jpg"));
					String request = new String(IOUtils.toByteArray(exchange.getRequestBody()));
					String method = exchange.getRequestMethod();
					String path = exchange.getRequestURI().getPath();
					logger.info("Method:{}, Request:{}, Path:{}", new Object[]{method, request, path});
//				headers.forEach((key, value)->{logger.info("{}-{}", new Object[]{key, value});});
//				byte[] response = "{\"success\": true}".getBytes();
					exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
					exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
					exchange.getResponseHeaders().add("Content-type", "application/json; charset=utf-8");
					
					if(HttpMethod.OPTIONS.toString().equalsIgnoreCase(method)){
						exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					}else if(request!=null && !request.isEmpty()){
						HashMap map = new ObjectMapper().readValue(request, HashMap.class);
						if(map.containsKey("operation") && "capture".equals(map.get("operation"))){
							Map<String, Object> result = new HashMap<String, Object>();
							result.put("state", "done");
							result.put("status", "success");
							result.put("operation", "capture");
							result.put("message", "Scan done");
							result.put("nfiq", "S1");
							result.put("id", new Random().nextInt());
							byte[] captureResult = new ObjectMapper().writeValueAsBytes(result);
							exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, captureResult.length);
							exchange.getResponseBody().write(captureResult);
						}else{
//							exchange.getResponseHeaders().add("Content-type", "image/*");
//							
//							exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length);
//							exchange.getResponseBody().write(file);
						}
					}else{
						exchange.getResponseHeaders().add("Content-type", "image/*");
						
						exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, file.length);
						exchange.getResponseBody().write(file);
					}
					exchange.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		httpServer.start();
		try {
			Thread.currentThread().join();
		} finally {
			System.out.println("Stop....");
			httpServer.stop(0);
		}
	}

	public static void main(String[] args) throws Exception {
		new FingerprintServerSim().start();
	}

}
