/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.signature;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Procesador de firmas primitivo, tipo PKCS#1.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 38 $
 */
public interface RawSignatureProcessor {

	/**
	 * Firmar
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	byte[] sign(byte[] data, PrivateKey privateKey);

	/**
	 * Verificar
	 * 
	 * @param data
	 * @param signature
	 * @param publicKey
	 * @return
	 */
	boolean verify(byte[] data, byte[] signature, PublicKey publicKey);

	/**
	 * Firmar
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	byte[] sign(InputStream data, PrivateKey privateKey);

	/**
	 * Verificar
	 * 
	 * @param data
	 * @param signature
	 * @param publicKey
	 * @return
	 */
	boolean verify(InputStream data, byte[] signature, PublicKey publicKey);
}