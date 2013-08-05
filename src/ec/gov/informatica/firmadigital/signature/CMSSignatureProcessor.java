/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.signature;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import ec.gov.informatica.firmadigital.DatosUsuario;

/**
 * Procesador de firmas tipo Cryptographic Message Syntax (CMS).
 * 
 * El estandar CMS esta descrito en el RFC 3852:
 * http://www.ietf.org/rfc/rfc3852.txt
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 15 $
 */
public interface CMSSignatureProcessor {

	/**
	 * Firmar.
	 * 
	 * @param data
	 * @param privateKey
	 * @param chain
	 * @return
	 */
	
	byte[] sign(byte[] data, PrivateKey privateKey, Certificate[] chain);

	/**
	 * Verifica la firma.
	 * 
	 * @param signedBytes
	 * @return
	 * @throws SignatureVerificationException
	 */
	byte[] verify(byte[] signedBytes) throws SignatureVerificationException;

	/**
	 * Agregar una firma a un archivo ya firmado.
	 * 
	 * @param signedBytes
	 * @param privateKey
	 * @param chain
	 * @return
	 */
	byte[] addSignature(byte[] signedBytes, PrivateKey privateKey, Certificate[] chain);

    /**
     * para tomar el certificado que arroja la verificacion
     */
    public X509Certificate getCert();
}