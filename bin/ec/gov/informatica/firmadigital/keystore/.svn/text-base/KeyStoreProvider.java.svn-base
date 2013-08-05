/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;

/**
 * Obtiene un KeyStore.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 15 $
 */
public interface KeyStoreProvider {

	/**
	 * Obtiene un KeyStore protegido por un password.
	 * 
	 * @param password
	 * @return
	 * @throws KeyStoreException
	 */
	KeyStore getKeystore(char[] password) throws KeyStoreException;
}