package ec.gov.informatica.firmadigital;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.digest.DigestUtils;

import sun.misc.BASE64Decoder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class JerseyClient {
	
	private static final String URI ="http://pruebascoactivas.espaciolink.com/webservices/index.php/procesoslegales/";

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
	
	public void guardarArchivo() {
		try {

			Client client = Client.create();
			client.addFilter(new LoggingFilter(System.out));
			WebResource webResource = client
					.resource(URI+"loadarchivos");

			MultivaluedMap queryParams = new MultivaluedMapImpl();
			queryParams.add("token", getToken());
//			 queryParams.add("hash", getHash());

			ClientResponse response = webResource.queryParams(queryParams)
					.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

//			String output = response.getEntity(String.class);
//			Gson gson = new Gson();
//			List<String> token = gson.fromJson(output, List.class);
			byte[] pdf;
//			if (token != null && !token.isEmpty()) {
				BASE64Decoder decoder = new BASE64Decoder();
//				return token.get(0);
//				pdf=decoder.decodeBuffer(token.get(0));
				String respuesta=response.getEntity(String.class);
				respuesta=respuesta.substring(1, respuesta.length()-1);
				respuesta=respuesta.replace("\\", "");
				System.out.println(respuesta);
//				respuesta="JVBERi0xLjQKJeLjz9MKMyAwIG9iago8PC9MZW5ndGggMzQxL0ZpbHRlci9GbGF0ZURlY29kZT4+c3RyZWFtCnicfZJNTsMwEIX3PsUsy4JgO6mdLFPEjyIVqSIXcIkpRWlC3RYkdhyFJVdgm4vhxA2CuBNFkSy/N9+zZ7wls5yEAmIqIC8IhfOEt4urnCzItvs5ZHb/htBgCm8kjoMQZEKBhUkQSWACjCb3R5VzGUiJ66GQAR+pd3QZjdMxvadjuqNP43E6pvd0THf0iI/TMb2nY7qjs9+b8fgUHdN7uq8v2gfAgNqPQUK74doXsCEX1wyYXT2SyV0dnOXP7Zv44+U8caP23Zul0X5BKBI3+2HBS6n2yve7w0jh3Lx3M/wonjddHdalMnCpTaFgpk2lTLGuaixM0AGA42Getw+7tTHNZ1Xod8jqXfMF6YnuubwoGjBCPM/zpuVGrQ7aQBtsjIa5MvsntJF24P/rIzzL8zYf5asy9kbzunpQtpfZQVUKim7Mw2rWjWGS6V3zvTuG/ABaqfdACmVuZHN0cmVhbQplbmRvYmoKNSAwIG9iago8PC9QYXJlbnQgNCAwIFIvQ29udGVudHMgMyAwIFIvVHlwZS9QYWdlL1Jlc291cmNlczw8L1Byb2NTZXQgWy9QREYgL1RleHQgL0ltYWdlQiAvSW1hZ2VDIC9JbWFnZUldL0ZvbnQ8PC9GMSAxIDAgUi9GMiAyIDAgUj4+Pj4vTWVkaWFCb3hbMCAwIDU5NSA4NDJdPj4KZW5kb2JqCjEgMCBvYmoKPDwvTGFzdENoYXIgMTE2L0Jhc2VGb250L1RpbWVzLUJvbGQvVHlwZS9Gb250L0VuY29kaW5nPDwvVHlwZS9FbmNvZGluZy9EaWZmZXJlbmNlc1s0Ni9wZXJpb2QgNzgvTiA5Ny9hL2IgMTAxL2UgMTA4L2wvbSAxMTEvby9wIDExNC9yIDExNi90XT4+L1N1YnR5cGUvVHlwZTEvV2lkdGhzWzI1MCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDcyMiAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCA1MDAgNTU2IDAgMCA0NDQgMCAwIDAgMCAwIDAgMjc4IDgzMyAwIDUwMCA1NTYgMCA0NDQgMCAzMzNdL0ZpcnN0Q2hhciA0Nj4+CmVuZG9iagoyIDAgb2JqCjw8L0xhc3RDaGFyIDE5NS9CYXNlRm9udC9UaW1lcy1Sb21hbi9UeXBlL0ZvbnQvRW5jb2Rpbmc8PC9UeXBlL0VuY29kaW5nL0RpZmZlcmVuY2VzWzMyL3NwYWNlIDQ2L3BlcmlvZCA0OS9vbmUvdHdvL3RocmVlL2ZvdXIgNjUvQS9CL0MgNzIvSCA3NC9KIDc3L00gOTcvYSA5OS9jL2QvZSAxMDMvZy9oL2kgMTA4L2wvbS9uL28gMTE0L3Ivcy90L3UvdiAxMjIveiAxMjkvLm5vdGRlZiAxNjEvLm5vdGRlZiAxNjkvLm5vdGRlZiAxODYvLm5vdGRlZiAxOTUvLm5vdGRlZl0+Pi9TdWJ0eXBlL1R5cGUxL1dpZHRoc1syNTAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAyNTAgMCAwIDUwMCA1MDAgNTAwIDUwMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCA3MjIgNjY3IDY2NyAwIDAgMCAwIDcyMiAwIDM4OSAwIDAgODg5IDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgNDQ0IDAgNDQ0IDUwMCA0NDQgMCA1MDAgNTAwIDI3OCAwIDAgMjc4IDc3OCA1MDAgNTAwIDAgMCAzMzMgMzg5IDI3OCA1MDAgNTAwIDAgMCAwIDQ0NCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwIDAgMCAwXS9GaXJzdENoYXIgMzI+PgplbmRvYmoKNCAwIG9iago8PC9JVFhUKDIuMS43KS9UeXBlL1BhZ2VzL0NvdW50IDEvS2lkc1s1IDAgUl0+PgplbmRvYmoKNiAwIG9iago8PC9UeXBlL0NhdGFsb2cvUGFnZXMgNCAwIFI+PgplbmRvYmoKNyAwIG9iago8PC9Qcm9kdWNlcihpVGV4dCAyLjEuNyBieSAxVDNYVCkvTW9kRGF0ZShEOjIwMTMwNjE5MTkwMDA4LTA1JzAwJykvQ3JlYXRpb25EYXRlKEQ6MjAxMzA2MTkxOTAwMDgtMDUnMDAnKT4+CmVuZG9iagp4cmVmCjAgOAowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDA1ODkgMDAwMDAgbiAKMDAwMDAwMDk0NyAwMDAwMCBuIAowMDAwMDAwMDE1IDAwMDAwIG4gCjAwMDAwMDE2NTMgMDAwMDAgbiAKMDAwMDAwMDQyMyAwMDAwMCBuIAowMDAwMDAxNzE2IDAwMDAwIG4gCjAwMDAwMDE3NjEgMDAwMDAgbiAKdHJhaWxlcgo8PC9Sb290IDYgMCBSL0lEIFs8MmRlNTgzMmFkNGE1YTRiMjgzNzkyYWIzOGRjNGZjNzA+PGE4NjhjNjY1NzAyYTc3ZTRkZjNlZTVkZTI5Y2NlMjRiPl0vSW5mbyA3IDAgUi9TaXplIDg+PgpzdGFydHhyZWYKMTg4MwolJUVPRgo=";
				pdf=decoder.decodeBuffer(respuesta);
				FileOutputStream fos = new FileOutputStream("prueba.pdf");
				fos.write(pdf);
				fos.close();
//			}
//			throw new Exception();

		} catch (Exception e) {

			e.printStackTrace();
//			return null;

		}

	}

}
