package ec.gov.informatica.firmadigital;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JerseyClient {
	
	private static final String URI ="http://pruebascoactivas.espaciolink.com/webservices/codeigniter/index.php/procesoslegales/";

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
					.resource(URI+"authenticate");

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

}
