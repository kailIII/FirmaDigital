/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.keystore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AuthProvider;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;

/**
 * Implementacion de <code>KeyStoreProvider</code> para utilizar con librerias
 * PKCS#11 del sistema operativo Linux.
 * 
 * Utiza OpenCT para acceder a un Token USB.
 * 
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 15 $
 */
public class LinuxKeyStoreProvider implements KeyStoreProvider {

        //MAC:   /usr/local/lib/libsfntpkcs11.dylib\nslot = 1
	private static final byte[] PKCS11_CONFIG = "name = SmartCard\nlibrary = /usr/local/SafeNet/lib/libsfntpkcs11.so\nslot = 1 \n showInfo = true".getBytes();  // /usr/local/SafeNet/lib/libsfntpkcs11.so\nslot = 1
                ///usr/local/lib/libsfntpkcs11.dylib
	private static final String SUN_PKCS11_PROVIDER_CLASS = "sun.security.pkcs11.SunPKCS11";
        private AuthProvider aprov;
	@Override
	public KeyStore getKeystore(char[] password) throws KeyStoreException {
		try {
			InputStream configStream = new ByteArrayInputStream(PKCS11_CONFIG);

			Provider sunPKCS11Provider = this.createSunPKCS11Provider(configStream);
			Security.addProvider(sunPKCS11Provider);

//                        Eestas lineas se aÃ±adieron para poder hacer un logout del keystore
//                     
                        
                        KeyStore.Builder ksBuilder = KeyStore.Builder.newInstance(
                            "PKCS11",
                            null,
                            new KeyStore.CallbackHandlerProtection( new SimpleCallbackHandler(null,password )   )); //cmdLineHdlr

                         KeyStore ks = ksBuilder.getKeyStore();
//       

                        aprov = (AuthProvider) Security.getProvider( sunPKCS11Provider.getName() );
                        aprov.setCallbackHandler( new SimpleCallbackHandler(null,password )   ); //cmdLineHdlr

            try {
                aprov.login(null, null);
            } catch (LoginException ex) {
                Logger.getLogger(LinuxKeyStoreProvider.class.getName()).log(Level.SEVERE, null, ex);
                throw new LoginException( /*ex*/ );
            }

//			KeyStore keyStore = KeyStore.getInstance("PKCS11");
//
//			keyStore.load(null, password); //null
                    System.out.println("Dentro LinuxKeystore anntes de ks:" + ks);
                       // aprov.logout();
			return ks;  //keyStore
//		} catch (CertificateException e) {
//			throw new KeyStoreException(e);
//		} catch (NoSuchAlgorithmException e) {
//			throw new KeyStoreException(e);
//		} catch (IOException e) {
//			throw new KeyStoreException(e);
//		}
                } catch (Exception e){
                    System.out.println("Error en el keystore:"  + e);
			throw new KeyStoreException(e);
		}
	}
        /**
         * <code> logout </code>
         *  Esta funciÃ³n permite limpiar de memoria el keystore.
         * @throws javax.security.auth.login.LoginException
         */
        public void logout() throws LoginException{

            this.aprov.logout();
        }
	/**
	 * Instancia la clase <code>sun.security.pkcs11.SunPKCS11</code>
	 * dinamicamente, usando Java Reflection.
	 * 
	 * @return una instancia de <code>sun.security.pkcs11.SunPKCS11</code>
	 */
	@SuppressWarnings("unchecked")
	private Provider createSunPKCS11Provider(InputStream configStream) throws KeyStoreException {
		try {
			Class sunPkcs11Class = Class.forName(SUN_PKCS11_PROVIDER_CLASS);
			Constructor pkcs11Constr = sunPkcs11Class.getConstructor(InputStream.class);
			Provider pkcs11Provider = (Provider) pkcs11Constr.newInstance(configStream);

			return pkcs11Provider;
		} catch (ClassNotFoundException e) {
			throw new KeyStoreException(e);
		} catch (NoSuchMethodException e) {
			throw new KeyStoreException(e);
		} catch (InvocationTargetException e) {
			throw new KeyStoreException(e);
		} catch (IllegalAccessException e) {
			throw new KeyStoreException(e);
		} catch (InstantiationException e) {
			throw new KeyStoreException(e);
		}
	}
}
