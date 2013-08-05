package ec.gov.informatica.firmadigital;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.logging.Logger;

import ec.gov.informatica.firmadigital.keystore.KeyStoreProvider;
import ec.gov.informatica.firmadigital.keystore.KeyStoreProviderFactory;
import ec.gov.informatica.firmadigital.xml.XmlSignature;

public class FirmaDigitalTransacciones {

	private static final Logger logger = Logger.getLogger(FirmaDigitalTransacciones.class.getName());

	private KeyStore keyStore;
	private PrivateKey privateKey;
	private X509Certificate cert;

	public FirmaDigitalTransacciones() {
		try {
			KeyStoreProvider keyStoreProvider = KeyStoreProviderFactory.createKeyStoreProvider();
			this.keyStore = keyStoreProvider.getKeystore(null);

			Enumeration<String> enumeration = keyStore.aliases();
			String alias = enumeration.nextElement();
			this.privateKey = (PrivateKey) keyStore.getKey(alias, null);

			Certificate[] certs = keyStore.getCertificateChain(alias);
			this.cert = (X509Certificate) certs[0];
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e); // FIXME
		}
	}

	public InformacionCertificado getInformacionCertificado() {
		return new InformacionCertificado(cert);
	}

	public String firmar(String xml) {
		try {
			XmlSignature xmlSignature = new XmlSignature();
			return xmlSignature.signXML(xml, cert, privateKey);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e); // FIXME
		} catch (Exception e) {
			throw new RuntimeException(e); // FIXME
		}
	}
}