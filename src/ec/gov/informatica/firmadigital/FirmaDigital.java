/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package ec.gov.informatica.firmadigital;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

import ec.gov.informatica.firmadigital.keystore.KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.LinuxKeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.WindowsJDK5KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.WindowsJDK5KeyStoreProvider_SD;
import ec.gov.informatica.firmadigital.keystore.WindowsJDK5KeyStoreProvider_iKey;
import ec.gov.informatica.firmadigital.signature.BouncyCastleSignatureProcessor;
import ec.gov.informatica.firmadigital.signature.CMSSignatureProcessor;
import ec.gov.informatica.firmadigital.signature.SignatureVerificationException;

/**
 * Permite firmar y verificar digitalmente el contenido de archivos.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 15 $
 */
public class FirmaDigital {

	private static final Logger logger = Logger.getLogger(FirmaDigital.class
			.getName());
	private DatosUsuario datosUsuarioActual = new DatosUsuario();

	public FirmaDigital() {
	}

	public DatosUsuario getDatosUsuarioActual() {
		return datosUsuarioActual;
	}

	public void setDatosUsuarioActual(DatosUsuario datosUsuarioActual) {
		this.datosUsuarioActual = datosUsuarioActual;
	}

	/**
	 * <code> crearDatosUsuario </code>
	 * 
	 * @param signingCert
	 * @return Esta funcion llena los datos del usuario encontrados en el
	 *         certificado
	 */
	public DatosUsuario crearDatosUsuario(X509Certificate signingCert) {
		// System.out.println("Libreria: Esta en crearDatosUsuario : ");

		// System.out.println(" Antigua Infra probando Datos User CEDULA: " +
		// signingCert.getExtensionValue("1.2.3.4.1"));
		// System.out.println(" Nueva Infra probando Datos User CEDULA: " +
		// (signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.1")));

		/* **************************************************************************************************************
		 * No existe la posibilidad de que se realice una firma si el
		 * certificado no tiene el campo de cedula, por esta razon el campo
		 * cedula será el validador del tipo de infraestructura que fue creado
		 * el certificado
		 * ********************************************************
		 * ******************************************************
		 */
		DatosUsuario datosUsuario = new DatosUsuario();

		if (signingCert.getExtensionValue("1.2.3.4.1") != null) { // esta sobre
																	// la
																	// antigua
																	// infraestructura
			System.out
					.println("- Certificado generado con OIDS de antigua infraestructura BCE ");
			datosUsuario.setCedula(new String(signingCert
					.getExtensionValue("1.2.3.4.1")).trim());

			if (signingCert.getExtensionValue("1.2.3.4.2") != null) {
				datosUsuario.setNombre(new String(signingCert
						.getExtensionValue("1.2.3.4.2")).trim());
			}
			if (signingCert.getExtensionValue("1.2.3.4.3") != null) {
				String txtApellido = new String(
						signingCert.getExtensionValue("1.2.3.4.3")).trim();
				if (signingCert.getExtensionValue("1.2.3.4.4") != null) {
					txtApellido = txtApellido
							+ " "
							+ new String(
									signingCert.getExtensionValue("1.2.3.4.4"))
									.trim();
				}
				datosUsuario.setApellido(txtApellido);
			}
			if (signingCert.getExtensionValue("1.2.3.4.6") != null) {
				datosUsuario.setInstitucion(new String(signingCert
						.getExtensionValue("1.2.3.4.6")).trim());
			}
			if (signingCert.getExtensionValue("1.2.3.4.5") != null) {
				datosUsuario.setCargo(new String(signingCert
						.getExtensionValue("1.2.3.4.5")).trim());
			}

			if (signingCert.getSerialNumber() != null) {
				datosUsuario
						.setSerial(signingCert.getSerialNumber().toString());
			}
		} else if (signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.1") != null) { // esta
																						// sobre
																						// la
																						// nueva
																						// infraestructura
			System.out
					.println("- Certificado generado con OIDS de nueva infraestructura BCE");
			datosUsuario.setCedula(new String(signingCert
					.getExtensionValue("1.3.6.1.4.1.37947.3.1")).trim());

			if (signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.2") != null) {
				datosUsuario.setNombre(new String(signingCert
						.getExtensionValue("1.3.6.1.4.1.37947.3.2")).trim());
			}
			if (signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.3") != null) {
				String txtApellido = new String(
						signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.3"))
						.trim();
				if (signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.4") != null) {
					txtApellido = txtApellido
							+ " "
							+ new String(
									signingCert
											.getExtensionValue("1.3.6.1.4.1.37947.3.4"))
									.trim();
				}
				datosUsuario.setApellido(txtApellido);
			}
			if (signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.6") != null) {
				datosUsuario.setInstitucion(new String(signingCert
						.getExtensionValue("1.3.6.1.4.1.37947.3.6")).trim());
			}
			if (signingCert.getExtensionValue("1.3.6.1.4.1.37947.3.5") != null) {
				datosUsuario.setCargo(new String(signingCert
						.getExtensionValue("1.3.6.1.4.1.37947.3.5")).trim());
			}

			if (signingCert.getSerialNumber() != null) {
				datosUsuario
						.setSerial(signingCert.getSerialNumber().toString());
			}
		} else {
			System.out
					.println("- Certificado generado con OIDS de infraestructura securityData");
			datosUsuario.setCedula(new String(signingCert
					.getExtensionValue("1.3.6.1.4.1.37746.3.1")).trim());

			if (signingCert.getExtensionValue("1.3.6.1.4.1.37746.3.2") != null) {
				datosUsuario.setNombre(new String(signingCert
						.getExtensionValue("1.3.6.1.4.1.37746.3.2")).trim());
			}
			if (signingCert.getExtensionValue("1.3.6.1.4.1.37746.3.3") != null) {
				String txtApellido = new String(
						signingCert.getExtensionValue("1.3.6.1.4.1.37746.3.3"))
						.trim();
				if (signingCert.getExtensionValue("1.3.6.1.4.1.37746.3.4") != null) {
					txtApellido = txtApellido
							+ " "
							+ new String(
									signingCert
											.getExtensionValue("1.3.6.1.4.1.37746.3.4"))
									.trim();
				}
				datosUsuario.setApellido(txtApellido);
			}
			if (signingCert.getExtensionValue("1.3.6.1.4.1.37746.3.6") != null) {
				datosUsuario.setInstitucion(new String(signingCert
						.getExtensionValue("1.3.6.1.4.1.37746.3.6")).trim());
			}
			if (signingCert.getExtensionValue("1.3.6.1.4.1.37746.3.5") != null) {
				datosUsuario.setCargo(new String(signingCert
						.getExtensionValue("1.3.6.1.4.1.37746.3.5")).trim());
			}

			if (signingCert.getSerialNumber() != null) {
				datosUsuario
						.setSerial(signingCert.getSerialNumber().toString());
			}

		}

		if (signingCert.getExtensionValue("2.5.29.31") != null) {

			// Nuevo codigo validacion CRL
			byte[] val1 = signingCert.getExtensionValue("2.5.29.31");
			if (val1 == null) // esta parte se puede omitir o se lo puede dejar
								// si se quiere tener un mayor control
			{
				if (signingCert.getSubjectDN().getName()
						.equals(signingCert.getIssuerDN().getName())) {
					System.out
							.println("El certificado es un certificado raiz: "
									+ signingCert.getSubjectDN().getName());
				} else {
					System.out
							.println("El certificado NO tiene punto de distribuciÃ³n de CRL : "
									+ signingCert.getSubjectDN().getName());
				}
				// return Collections.emptyList();
			} else {
				// Obtiene dentro del certificado del token la lista de
				// distribución CRL usada para consultar el LDAP del BCE.
				try {
					ASN1InputStream oAsnInStream = new ASN1InputStream(
							new ByteArrayInputStream(val1));
					DERObject derObj = oAsnInStream.readObject();
					DEROctetString dos = (DEROctetString) derObj;
					byte[] val2 = dos.getOctets();
					ASN1InputStream oAsnInStream2 = new ASN1InputStream(
							new ByteArrayInputStream(val2));
					DERObject derObj2 = oAsnInStream2.readObject();
					List<String> urls = getDERValue(derObj2);

					for (int j = 0; j < urls.size(); j++) {
						datosUsuario.setCrl(urls.get(7));
					}
					// datosUsuario.setCrl( new String( distrPoint.substring(
					// distrPoint.indexOf("U")+8,
					// distrPoint.indexOf("ldap") - 8 ) ).trim() );
					// //distrPoint.substring( distrPoint.indexOf("U")+8,
					// distrPoint.indexOf("U") + 12 )
					System.out
							.println("- Informacion contenida en el Certificado : > "
									+ urls + "\n");// .println(urls);
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
					e.printStackTrace();
				}
			} // fin else
				// Fin validacion CRL

			// System.out.println("Dist_point:" + distrPoint );
			// OJO : Esta validacion puede fallar si la lista de distribucion
			// crece a dos digitos . REVISAR
			// datosUsuario.setCrl( new String( distrPoint.substring(
			// distrPoint.indexOf("U")+8, distrPoint.indexOf("U")
			// + 12 ) ).trim() );
		}
		return datosUsuario;
	}

	/**
	 * para parsear el objeto y te devuelve el listado con las urls de los
	 * puntos de distribución
	 * 
	 * @param derObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> getDERValue(DERObject derObj) {
		if (derObj instanceof DERSequence) {
			List<String> list = new LinkedList<String>();
			DERSequence seq = (DERSequence) derObj;
			Enumeration enumeracion = seq.getObjects();
			while (enumeracion.hasMoreElements()) {
				DERObject nestedObj = (DERObject) enumeracion.nextElement();
				List<String> appo = getDERValue(nestedObj);
				if (appo != null) {
					list.addAll(appo);
				}
			}
			return list;
		} else if (derObj instanceof DERTaggedObject) {
			DERTaggedObject derTag = (DERTaggedObject) derObj;
			if ((derTag.isExplicit() && !derTag.isEmpty())
					|| derTag.getObject() instanceof DERSequence) {
				DERObject nestedObj = derTag.getObject();
				List<String> ret = getDERValue(nestedObj);
				return ret;
			} else {
				DEROctetString derOct = (DEROctetString) derTag.getObject();
				String val = new String(derOct.getOctets());
				List<String> ret = new LinkedList<String>();
				ret.add(val);
				return ret;
			}
		} else if (derObj instanceof DERSet) {
			Enumeration enumSet = ((DERSet) derObj).getObjects();
			List<String> list = new LinkedList<String>();
			while (enumSet.hasMoreElements()) {
				DERObject nestedObj = (DERObject) enumSet.nextElement();
				List<String> appo = getDERValue(nestedObj);
				if (appo != null) {
					list.addAll(appo);
				}
			}
			return list;
		} else if (derObj instanceof DERObjectIdentifier) {
			DERObjectIdentifier derId = (DERObjectIdentifier) derObj;
			List<String> list = new LinkedList<String>();
			list.add(derId.getId());
			return list;
		} else if (derObj instanceof DERPrintableString) {
			// hemos localizado un par id-valor
			String valor = ((DERPrintableString) derObj).getString();
			List<String> list = new LinkedList<String>();
			list.add(valor);
			return list;
		} else {
			System.out
					.println("tipo de dato en ASN1 al recuperar las crls no es reconocido : "
							+ derObj);
		}
		return null;
	}

	/**
	 * Llama a un servico web expuesto por la subsecretaria de informatica el
	 * cual validara si el serial de un certificado está o no en la lsiat de
	 * ravocados
	 * 
	 * @return Fecha de revocacion del certificado, Vacio en caso de no estar
	 *         revocado,
	 */
	public String verificaRevocados(String serial, String tipo) {
		System.out.println("- Verificando revocados para: " + serial);
		String resultado = "";
		String entidadCertificadora = "1";
		if (tipo.equals("3")) {
			entidadCertificadora = "2";
		}
		try { // llamada al servico web
			ec.gob.informatica.firmadigitalservice.FirmaDigitalService service = new ec.gob.informatica.firmadigitalservice.FirmaDigitalService();
			ec.gob.informatica.firmadigitalservice.FirmaDigital port = service
					.getFirmaDigitalSoap();
			resultado = port.verificarRevocados(serial, entidadCertificadora,
					"");
			return resultado;
		} catch (Exception ex) {
			return "ERROR EN CONSULTA";
		}
	}

	/**
	 * Firma un archivo.
	 * 
	 * @param data
	 * @return
	 */
	public byte[] firmar(byte[] data, String claveToken,
			String tipoCertificado, String urlCertificado) {
		try {
			KeyStore keyStore = null;
			Enumeration<String> enumeration = null;
			String alias = null;
			PrivateKey privateKey = null;
			Certificate[] certs = null;
			CMSSignatureProcessor cms = null;
			KeyStoreProvider keyStoreProvider = null;
			try {
				if (tipoCertificado.equals("1") || tipoCertificado.equals("2")
						|| tipoCertificado.equals("3")) {
					System.out.println("- Firmando con certificado token."
							+ tipoCertificado);
					keyStoreProvider = this
							.getKeyStoreProvider(tipoCertificado);
					keyStore = keyStoreProvider.getKeystore(claveToken
							.toCharArray());
					enumeration = keyStore.aliases();
					alias = enumeration.nextElement();
					privateKey = (PrivateKey) keyStore.getKey(alias, null);
					cms = new BouncyCastleSignatureProcessor(keyStore);
				}
				// if (tipoCertificado.equals("4")) {
				// System.out.println("- Firmando con certificado en archivo.");
				// keyStore = java.security.KeyStore.getInstance("PKCS12"); //
				// instancia el ks
				// keyStore.load(new java.io.FileInputStream(urlCertificado),
				// claveToken.toCharArray());
				// Enumeration en = keyStore.aliases();
				// alias = "";
				// Vector vectaliases = new Vector();
				// while (en.hasMoreElements()) {
				// vectaliases.add(en.nextElement());
				// }
				// String[] aliases = (String[]) (vectaliases.toArray(new
				// String[0]));
				// for (int i = 0; i < aliases.length; i++) {
				// if (keyStore.isKeyEntry(aliases[i])) {
				// alias = aliases[i];
				// break;
				// }
				// }
				// privateKey = (PrivateKey) keyStore.getKey(alias,
				// claveToken.toCharArray());
				// cms = new BouncyCastleSignatureProcessor(keyStore);
				// }
			} catch (Exception e) {
				System.out.println(" \n Fallo trayendo keystore "
						+ e.getMessage());
			}
			certs = keyStore.getCertificateChain(alias);
			Certificate[] chain = keyStore.getCertificateChain(alias);
			PrivateKey key = (PrivateKey) keyStore.getKey(alias,
					claveToken.toCharArray());
			String revocados = ""; // para verificar revocados
			revocados = verificaRevocados(((X509Certificate) certs[0])
					.getSerialNumber().toString(), tipoCertificado);
			if (!revocados.isEmpty()) {
				System.out.println(" CERTIFICADO REVOCADO " + revocados);
				return null;
			}
			System.out.println("- Certificado valido ");

			PdfReader reader = new PdfReader(
					"C:\\Users\\hp1\\Dropbox\\Profesional\\aprendizaje\\1932394850Java.pdf");
			FileOutputStream fout = new FileOutputStream(
					"C:\\Users\\hp1\\Dropbox\\Profesional\\aprendizaje\\1932394850JavaFirmado.pdf");
			PdfStamper stp = PdfStamper.createSignature(reader, fout, '?');
			PdfSignatureAppearance sap = stp.getSignatureAppearance();
			sap.setCrypto(key, chain, null,
					PdfSignatureAppearance.WINCER_SIGNED);
			sap.setReason("Firma PKCS12");
			sap.setLocation("Imaginanet");
			// A�ade la firma visible. Podemos comentarla para que no sea
			// visible.
			sap.setVisibleSignature(new Rectangle(100, 100, 200, 200), 1, null);
			stp.close();

			byte[] datosFirmados = cms.sign(data, privateKey, certs);
			System.out.println("Firmado Correctamente..!");
			this.datosUsuarioActual = this
					.crearDatosUsuario((X509Certificate) certs[0]); // llena la
																	// clase de
																	// tipo
																	// datosUsuario
																	// con el
																	// certificado
																	// actual

			return datosFirmados;
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e); // FIXME
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Firma un archivo.
	 * 
	 * @param data
	 * @return
	 */
	public DatosUsuario login(String claveToken) {
		try {
			KeyStore keyStore = null;
			Enumeration<String> enumeration = null;
			String alias = null;
			PrivateKey privateKey = null;
			Certificate[] certs = null;
			CMSSignatureProcessor cms = null;
			KeyStoreProvider keyStoreProvider = null;
			String tipoCertificado = "2";
			try {

				if (tipoCertificado.equals("1") || tipoCertificado.equals("2")
						|| tipoCertificado.equals("3")) {
					System.out.println("- Firmando con certificado token."
							+ tipoCertificado);
					keyStoreProvider = this
							.getKeyStoreProvider(tipoCertificado);
					keyStore = keyStoreProvider.getKeystore(claveToken
							.toCharArray());
					enumeration = keyStore.aliases();
					alias = enumeration.nextElement();
					privateKey = (PrivateKey) keyStore.getKey(alias, null);
					cms = new BouncyCastleSignatureProcessor(keyStore);
				}

			} catch (Exception e) {
				System.out.println(" \n Fallo trayendo keystore "
						+ e.getMessage());
				return null;
			}

			certs = keyStore.getCertificateChain(alias);
			String revocados = ""; // para verificar revocados
			revocados = verificaRevocados(((X509Certificate) certs[0])
					.getSerialNumber().toString(), tipoCertificado);
			if (!revocados.isEmpty()) {
				System.out.println(" CERTIFICADO REVOCADO " + revocados);
				return null;
			}

			this.datosUsuarioActual = this
					.crearDatosUsuario((X509Certificate) certs[0]); // llena la
																	// clase de
																	// tipo
																	// datosUsuario
																	// con el
																	// certificado
																	// actual

			return this.datosUsuarioActual;

		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e); // FIXME
		}
	}

	/**
	 * Verifica un archivo firmado y obtiene su contenido original.
	 * 
	 * @param data
	 * @return
	 * @throws SignatureVerificationException
	 */
	public byte[] verificar(byte[] data) throws SignatureVerificationException {
		try {
			CMSSignatureProcessor cms = new BouncyCastleSignatureProcessor(null); // keyStore
			byte[] verificado = cms.verify(data);
			this.datosUsuarioActual = this.crearDatosUsuario(cms.getCert()); // llena
																				// la
																				// clase
																				// de
																				// tipo
																				// datosUsuario
																				// con
																				// el
																				// certificado
																				// actual
			return verificado;
		} catch (Exception e) {
			System.out.println("Error:" + e);
			throw new RuntimeException(e);
		}
		/*
		 * catch (GeneralSecurityException e) { throw new RuntimeException(e);
		 * // FIXME }
		 */
	}

	public List<String> verificar(String direccionPDF)
			throws SignatureVerificationException {
		try {
			List<String> firmantes=new ArrayList<>();
			if (direccionPDF == null || direccionPDF.isEmpty()) {
				System.out.print("Necesito el nombre del PDF a comprobar");
				System.exit(1);
			}

			Random rnd = new Random();
			KeyStore kall = PdfPKCS7.loadCacertsKeyStore();
			PdfReader reader = new PdfReader(direccionPDF);
			AcroFields af = reader.getAcroFields();
			ArrayList names = af.getSignatureNames();
			for (int k = 0; k < names.size(); ++k) {

				String name = (String) names.get(k);
//				System.out.println(name);
				int random = rnd.nextInt();
				FileOutputStream out = new FileOutputStream("revision_"
						+ random + "_" + af.getRevision(name) + ".pdf");

				byte bb[] = new byte[8192];
				InputStream ip = af.extractRevision(name);
				int n = 0;
				while ((n = ip.read(bb)) > 0)
					out.write(bb, 0, n);
				out.close();
				ip.close();

				PdfPKCS7 pk = af.verifySignature(name);
				Calendar cal = pk.getSignDate();
				Certificate pkc[] = pk.getCertificates();
				Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null,
						cal);
				String firmante=pk.getSignName()+" ("+name+") - ";
				if (fails == null) {
					firmante +="Firma Verificada";
				} else {
					firmante +="Firma No V�lida";
				}
				File f = new File("revision_" + random + "_"
						+ af.getRevision(name) + ".pdf");
				f.delete();
				firmantes.add(firmante);
			}
			return firmantes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Obtiene la implementacion correcta de KeyStoreProvider de acuerdo al
	 * sistema operativo.
	 * 
	 * @return implementacion de KeyStoreProvider
	 */
	private KeyStoreProvider getKeyStoreProvider(String tipo) {
		String osName = System.getProperty("os.name");

		if (osName.toUpperCase().indexOf("WINDOWS") == 0) {
			if (tipo.equals("2")) {
				return new WindowsJDK5KeyStoreProvider(); // trabaja con
															// librerias sera
															// para eToken
			}
			if (tipo.equals("1")) {
				return new WindowsJDK5KeyStoreProvider_iKey(); // trabaja con
																// librerias
																// sera para
																// iKey
			}
			if (tipo.equals("3")) {
				return new WindowsJDK5KeyStoreProvider_SD(); // trabaja con
																// librerias
																// sera para
																// iKey
			}
			return new WindowsJDK5KeyStoreProvider();
		} else {
			return new LinuxKeyStoreProvider();
		}
	}
}