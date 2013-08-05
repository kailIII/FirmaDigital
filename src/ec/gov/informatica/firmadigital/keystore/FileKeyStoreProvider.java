/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Implementacion de KeyStoreProvider para leer de un archivo.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 15 $
 */
public class FileKeyStoreProvider implements KeyStoreProvider {

	@Override
	public KeyStore getKeystore(char[] password) throws KeyStoreException {
		try {
			InputStream input = new FileInputStream(System.getProperty("user.home") + File.separator + ".keystore");

			KeyStore keyStore = KeyStore.getInstance("jks");
			keyStore.load(input, password);
			return keyStore;
		} catch (FileNotFoundException e) {
			throw new KeyStoreException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e);
		} catch (IOException e) {
			throw new KeyStoreException(e);
		} finally {
			// TODO: Close InputStream
		}
	}
}