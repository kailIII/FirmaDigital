/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;

/**
 * Implementacion de KeyStoreProvider para pruebas.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 15 $
 */
public class MockKeyStoreProvider implements KeyStoreProvider {

	private static final String KEYSTORE_FILE = "/home/ricardo/ricardostore";

	// private static final String KEYSTORE_ALIAS = "firmadigital";
	// private static final char[] KEYSTORE_PASSWORD = "abc123".toCharArray();
	// private static final char[] KEY_PASSWORD = "ricardo".toCharArray();

	@Override
	public KeyStore getKeystore(char[] password) throws KeyStoreException {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			FileInputStream ksfis;
			ksfis = new FileInputStream(KEYSTORE_FILE);
			BufferedInputStream ksbufin = new BufferedInputStream(ksfis);

			keyStore.load(ksbufin, password);
			// PrivateKey priv = (PrivateKey) keyStore.getKey(KEYSTORE_ALIAS,
			// KEY_PASSWORD);

			return keyStore;
		} catch (FileNotFoundException e) {
			throw new KeyStoreException(e);
		} catch (GeneralSecurityException e) {
			throw new KeyStoreException(e);
		} catch (IOException e) {
			throw new KeyStoreException(e);
		}
	}
}