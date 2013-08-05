/*
 * Copyright (C) 2009 Libreria para Firma Digital development team.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */

package ec.gov.informatica.firmadigital.cert;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

/**
 * Obtiene un certificado desde el LDAP del Banco Central.
 * 
 * @author Ricardo Arguello <ricardo.arguello@soportelibre.com>
 * @version $Revision: 1 $
 */
public class ServicioCertificadoBancoCentral {

	/** Servidor LDAP publico del Banco Central */
	private static final String LDAP_BCE = "ldap://ldap.bce.ec:389/o=BCE,c=EC";
        private static final String LDAP_BCE_CRL = "ldap://ldap.bce.ec/o=BCE,c=EC";  //ldap://ldap.bce.ec/o=BCE,c=EC  //-- ldap://157.100.207.69/o=BCETEST,c=EC

	private static final Logger logger = Logger.getLogger(ServicioCertificadoBancoCentral.class.getName());

	/**
	 * Busca un certificado en el LDAP del Banco Central.
	 * 
	 * @param numeroRuc
	 *            numero de RUC (opcional)
	 * @param cedula
	 *            numero de cedula (opcional)
	 * @return
	 */
	public X509Certificate obtenerCertificado(String numeroRuc, String cedula) {
		// Establecer el entorno para crear el InitialContext
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, LDAP_BCE);
		env.put(Context.SECURITY_AUTHENTICATION, "none");

		try {
			DirContext ctx = new InitialDirContext(env);
			Attributes matchAttrs = new BasicAttributes(true);

			if (numeroRuc != null) {
				matchAttrs.put(new BasicAttribute("RUC", numeroRuc));
			}

			if (cedula != null) {
				matchAttrs.put(new BasicAttribute("cedulaC", cedula));
			}
                        //Con esto si funciona .
                        //deberia haber una opcion que busque por cn  
                        //--matchAttrs.put(new BasicAttribute("cn", cn));

			// Buscar objetos que tengan los atributos
			NamingEnumeration<SearchResult> answer = ctx.search("ou=ECI", matchAttrs);

			if ((answer != null) && (answer.hasMoreElements())) {
				Attributes attribs = ((SearchResult) answer.next()).getAttributes();

				logger.fine(attribs.toString());
				// NamingEnumeration<String> enumer = attribs.getIDs();

				Attribute attribCertificate = attribs.get("userCertificate;binary");

				if (attribCertificate != null) {
					byte[] certificate = (byte[]) attribCertificate.get();
					InputStream is = new ByteArrayInputStream(certificate);
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					return (X509Certificate) cf.generateCertificate(is);
				}
			}

			return null;
		} catch (NamingException e) {
			// FIXME
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			// FIXME
			throw new RuntimeException(e);
		}
	}
        
        /**
         *  <code> verificarRevocados </code>
         * Esta función se encarga de verificar si el certificado (contenido dentro del token) es valido
         * @param numeroSerial Es el codigo del certificado
         * @param nombreListaCRL La lista CRL en la que se encuentra el certificado
         *  Para obtener el valor CRL hay que ir a Detalles del Certificado-> Punto de Distribucion CRL
         *  [1]Punto de distribución CRL
         *   Nombre del punto de distribución:
         *    Nombre completo:
         *      Dirección del directorio:
         *           CN=CRL1   (Este es el valor que debe usarse para nombreListaCRL)
         *           OU=eci
         *           O=bce
         *           C=ec
         * @return Devuelve una cadena si el certificado es revocado
         */
        public String verificarRevocados( BigInteger numeroSerial , String nombreListaCRL )  {
		// Establecer el entorno para crear el InitialContext
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, LDAP_BCE_CRL);
		env.put(Context.SECURITY_AUTHENTICATION, "none");

		try {
			DirContext ctx = new InitialDirContext(env);
			Attributes matchAttrs = new BasicAttributes(true);

			if ( nombreListaCRL != null) {
				matchAttrs.put(new BasicAttribute("cn", nombreListaCRL ));
			}

//			if (cedula != null) {
//				matchAttrs.put(new BasicAttribute("cn", cedula));
//			}
                        //Con esto si funciona .
                        //deberia haber una opcion que busque por cn  
                        //--matchAttrs.put(new BasicAttribute("cn", cn));

			// Buscar objetos que tengan los atributos
			//NamingEnumeration<SearchResult> answer = ctx.search("ou=ECITEST", matchAttrs);
                        NamingEnumeration<SearchResult> answer = ctx.search("ou=ECI", matchAttrs);
			if ((answer != null) && (answer.hasMoreElements())) 
                        {
                            
				Attributes attribs = ((SearchResult) answer.next()).getAttributes();

				logger.fine(attribs.toString());
				// NamingEnumeration<String> enumer = attribs.getIDs();

				Attribute attribCertificate = attribs.get("certificateRevocationList;binary"); //userCertificate;binary

				if (attribCertificate != null) {
					byte[] certificate = (byte[]) attribCertificate.get();
					InputStream is = new ByteArrayInputStream(certificate);
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
                                        
                                        //Esto es para probar Revocados
                                        
                                          X509CRL crl = (X509CRL)cf.generateCRL(is);
                                    System.out.println("CRL er     -->"+crl.getIssuerDN());
                                    System.out.println("Effective From -->"+crl.getThisUpdate());
                                    System.out.println("Next ate    -->"+crl.getNextUpdate());
                                    is.close();
                                    X509CRLEntry revocado = crl.getRevokedCertificate( numeroSerial );
                                    if ( revocado == null ){
                                        System.out.println(" NOT REVOKED...........");
                                        return "";
                                    }
                                    else
                                        return "Certificado revocado";
//                                    if (crl.isRevoked(  ) ) {
//                                        System.out.println("REVOKED...........");
//                                    } else {
//                                        System.out.println(" NOT REVOKED...........");
//                                    }
//                                  } catch(Exception e) {
//                                    e.printStackTrace();
//                                  }

					//return (X509Certificate) cf.generateCertificate(is);
				}
			}

			
		
        } catch (IOException ex) {
            Logger.getLogger(ServicioCertificadoBancoCentral.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error IO:" + ex);
        } catch (CRLException ex) {
            Logger.getLogger(ServicioCertificadoBancoCentral.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error crl:"+ ex);
        } catch (NamingException e) {
			// FIXME
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			// FIXME
			throw new RuntimeException(e);
		}
                return null;
	}
        
         /**
         *  <code> verificarRevocadosHttp </code>
         * Esta función se encarga de verificar si el certificado (contenido dentro del token) es valido usa 
          * la dirección  http://www.eci.bce.ec/CRL/eci_bce_ec_crlfile.crl, para buscar los revocados proporciada por el BCE
         * @param numeroSerial Es el codigo del certificado
         * 
         * @return Devuelve una cadena si el certificado es revocado
         */
        public String verificarRevocadosHttp( BigInteger numeroSerial  )  {
            try {
                
                
                //BigInteger numeroSerial = BigInteger.valueOf(1224799076);
                
                URL u = new URL("http://www.eci.bce.ec/CRL/eci_bce_ec_crlfile.crl");
                URLConnection uc = u.openConnection();
                String contentType = uc.getContentType();
                int contentLength = uc.getContentLength();
                if (contentType.startsWith("text/") || contentLength == -1) {
                    throw new IOException("This is not a binary file.");
                }
                InputStream raw = uc.getInputStream();
                InputStream in = new BufferedInputStream(raw);
                byte[] data = new byte[contentLength];
                int bytesRead = 0;
                int offset = 0;
                while (offset < contentLength) {
                    bytesRead = in.read(data, offset, data.length - offset);
                    if (bytesRead == -1) {
                        break;
                    }
                    offset += bytesRead;
                }
                in.close();

                if (offset != contentLength) {
                    throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
                }
                
                String filename = u.getFile(); // Esto puede fallar
                  //Averiguar el nombre del archivo
//                 filename = u.getFile().substring(filename.lastIndexOf('/') + 1);
//                 System.out.println("Filename: " + filename );
//                FileOutputStream out = new FileOutputStream(filename);
//                
                
                
                InputStream is = new ByteArrayInputStream ( data );
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
                                        
                                        //Esto es para probar Revocados
                                        
                                          X509CRL crl = (X509CRL)cf.generateCRL(is);
                                    System.out.println("CRL er     -->"+crl.getIssuerDN());
                                    System.out.println("Effective From -->"+crl.getThisUpdate());
                                    System.out.println("Next ate    -->"+crl.getNextUpdate());
                                    is.close();
                                    X509CRLEntry revocado = crl.getRevokedCertificate( numeroSerial );
                                    if ( revocado == null ){
                                        System.out.println(" NOT REVOKED...........");
                                        System.out.println("Certificado NO revocado");
                                        return "";
                                        
                                    }
                                    else
                                    {    
                                        System.out.println("Certificado Revocado...");
                                        return "Certificado revocado";
                                    }    
                
                /*
                String filename = u.getFile(); // Esto puede fallar
                  //Averiguar el nombre del archivo
                 filename = u.getFile().substring(filename.lastIndexOf('/') + 1);
                 System.out.println("Filename: " + filename );
                FileOutputStream out = new FileOutputStream(filename);
                out.write(data);
                out.flush();
                out.close();
                 */ 
            } catch (CRLException ex) {
                Logger.getLogger(ServicioCertificadoBancoCentral.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CertificateException ex) {
                Logger.getLogger(ServicioCertificadoBancoCentral.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ServicioCertificadoBancoCentral.class.getName()).log(Level.SEVERE, null, ex);

            } 
            return null;
        }
}