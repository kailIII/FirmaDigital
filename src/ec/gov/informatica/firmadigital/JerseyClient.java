package ec.gov.informatica.firmadigital;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JerseyClient {
	
	private static final String URI ="http://pruebascoactivas.espaciolink.com/";
	private static final String RESUMEN ="webservices/index.php/procesoslegales/resumenprocesolegal";
	private static final String LISTA_PDF ="webservices/index.php/procesoslegales/loadarchivos";
	private static final String PATH_PDF ="webservices/index.php/procesoslegales/obtenerArchivo";
	private static final String AUTHENTICATE ="webservices/index.php/procesoslegales/authenticate";
	private static final String SUBIR_ARCHIVO ="webservices/index.php/procesoslegales/subirArchivo";
	private FirmaDigital firmaDigital = new FirmaDigital();

	public String getHash() throws IOException {
		Date d = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		String content = DigestUtils.md5Hex(getPublicKey()
				+ dateformat.format(d) + getPrivateKey());
		System.out.println(content);
		return content;

	}

	public String getPrivateKey() throws IOException {
		// System.out.println("Working Directory = " +
		// System.getProperty("user.dir"));
		String content = Files.toString(new File("privateKeyContent.txt"),
				Charsets.UTF_8);
		System.out.println(content);

		return content;
	}

	public String getPublicKey() throws IOException {
		String content = Files.toString(new File("publicKeyContent.txt"),
				Charsets.UTF_8);
		System.out.println(content);
		return content;
	}
	
	

	public String getToken() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource(URI+AUTHENTICATE);

			MultivaluedMap queryParams = new MultivaluedMapImpl();
			queryParams.add("pk", getPublicKey());
			queryParams.add("hash", getHash());

			ClientResponse response = webResource.queryParams(queryParams)
					.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);
			Gson gson = new Gson();
			List<String> token = gson.fromJson(output, List.class);
			if (token != null && !token.isEmpty()) {
				return token.get(0);
			}
			throw new Exception();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}

	}
	
	public List<ResumenRow> subirArchivo(String id){
		try {

			Client client = Client.create();
			client.addFilter(new LoggingFilter(System.out));
			WebResource webResource = client
					.resource(URI+SUBIR_ARCHIVO);

			MultivaluedMap queryParams = new MultivaluedMapImpl();
			queryParams.add("token", getToken());
			 queryParams.add("id", id);
			 
			 String input = "{\"token\":\""+getToken()+"\",\"id\":\"Fade To Black\"}";

			ClientResponse response = webResource.queryParams(queryParams)
					.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
		    JsonArray array = parser.parse(output).getAsJsonArray();
		    List<ResumenRow> resumenRows= new ArrayList<ResumenRow>();
		    for (int i=0; i<array.size();i++){
		    	ResumenRow resumenRow = gson.fromJson(array.get(i), ResumenRow.class);
		    	resumenRows.add(resumenRow);
		    }
//			List<PdfRow> pdfRows = gson.fromJson(output, List.class);
			return resumenRows;

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}
	}
	
	
	public List<ResumenRow> getResumenRows(String ci){
		try {

			Client client = Client.create();
			client.addFilter(new LoggingFilter(System.out));
			WebResource webResource = client
					.resource(URI+RESUMEN);

			MultivaluedMap queryParams = new MultivaluedMapImpl();
			queryParams.add("token", getToken());
			 queryParams.add("ci", ci);

			ClientResponse response = webResource.queryParams(queryParams)
					.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
		    JsonArray array = parser.parse(output).getAsJsonArray();
		    List<ResumenRow> resumenRows= new ArrayList<ResumenRow>();
		    for (int i=0; i<array.size();i++){
		    	ResumenRow resumenRow = gson.fromJson(array.get(i), ResumenRow.class);
		    	resumenRows.add(resumenRow);
		    }
//			List<PdfRow> pdfRows = gson.fromJson(output, List.class);
			return resumenRows;

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}
	}
	
	public void firmarArchivos(List<IdentificacionPdf> archivosAFirmar, String claveToken) throws IOException{
		
		for (IdentificacionPdf identificacionPdf:archivosAFirmar) {
			String path=getObtenerPath(identificacionPdf.getIdCola(), identificacionPdf.getIdPdf());
			bajarArchivo(path,
					identificacionPdf.getIdCola() + "-" + identificacionPdf.getIdPdf()
					+ "-" + identificacionPdf.getNombrePdf());
			firmaDigital.firmar(claveToken, "2", identificacionPdf.getIdCola() + "-" + identificacionPdf.getIdPdf()
					+ "-" + identificacionPdf.getNombrePdf());
			
			
		}
	}
	
	public void bajarArchivo(String path, String fileName) throws IOException{
		
		URL website = new URL(URI+path);
	    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
	    FileOutputStream fos = new FileOutputStream(fileName);
	    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	    fos.close();
		
		
	}
	public String getObtenerPath(String idCola, String idAdjunto){
		try {

			Client client = Client.create();
			client.addFilter(new LoggingFilter(System.out));
			WebResource webResource = client
					.resource(URI+PATH_PDF);

			MultivaluedMap queryParams = new MultivaluedMapImpl();
			queryParams.add("token", getToken());
			 queryParams.add("id", idCola);
			 queryParams.add("idPdf", idAdjunto);

			ClientResponse response = webResource.queryParams(queryParams)
					.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);
			Scanner sc = new Scanner(output);
			String path="";
			while (sc.hasNextLine()) {
//			    Syarstem.out.println("[" + sc.nextLine() + "]");
			    path=sc.nextLine();
			}
			
			sc.close();
			path = path.replace("\\", "");
			path = path.replace("/home/coactiva/public_html/", "");
			path = path.replace("\"", "");
			System.out.println(path);
			return path;

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}
	}
	public List<PdfRow> getPdfRows(String ci,Integer idProceso){
		try {

			Client client = Client.create();
			client.addFilter(new LoggingFilter(System.out));
			WebResource webResource = client
					.resource(URI+LISTA_PDF);

			MultivaluedMap queryParams = new MultivaluedMapImpl();
			queryParams.add("token", getToken());
			 queryParams.add("idProceso", idProceso.toString());
			 queryParams.add("ci", ci);

			ClientResponse response = webResource.queryParams(queryParams)
					.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			String output = response.getEntity(String.class);
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
		    JsonArray array = parser.parse(output).getAsJsonArray();
		    List<PdfRow> pdfRows= new ArrayList<PdfRow>();
		    for (int i=0; i<array.size();i++){
		    	PdfRow pdfRow = gson.fromJson(array.get(i), PdfRow.class);
//		    	pdfRow.generarBytes();
		    	pdfRows.add(pdfRow);
		    }
//			List<PdfRow> pdfRows = gson.fromJson(output, List.class);
			return pdfRows;

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		}

	}

}
