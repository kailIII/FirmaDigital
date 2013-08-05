/** Programa para La firma electrónica de archivos
 * Desarrollado y Modificado por la Subsecretaría de Tecnologías de la Información
 * de la Secretaría Nacional de la Administración Pública del Ecuador
 * Firma Digital firmadigital.informatica.gob.ec
 *------------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or
modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
License
 * along with this program. If not, see http://www.gnu.org/licenses. [^]
 *------------------------------------------------------------------------------
 **/
package ec.gov.informatica.firmadigital.keystore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AuthProvider;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ReflectionException;
import javax.security.auth.login.LoginException;

/**
 * maneja el keystore de windows, no el CAPI, accede a travez de librerias, en esta se usa librerias para eTokenPro
 * @author SNAP-STI <http://informatica.gob.ec/>
 **/
public class WindowsJDK5KeyStoreProvider implements KeyStoreProvider {


    //private static final byte[] PKCS11_CONFIG = "name = SmartCard\nlibrary = C:\\WINDOWS\\SYSTEM32\\DKCK201.DLL \ndisabledMechanisms = { CKM_SHA1_RSA_PKCS  }".getBytes();
    private static String windowsDir = "name = SmartCard\nlibrary = " + System.getenv("WINDIR") + "\\SYSTEM32\\eTPKCS11.DLL \ndisabledMechanisms = { CKM_SHA1_RSA_PKCS  } \n showInfo = true"; //showInfo = true
    private static final byte[] PKCS11_CONFIG = windowsDir.getBytes();
    private static final String SUN_PKCS11_PROVIDER_CLASS = "sun.security.pkcs11.SunPKCS11";
    private AuthProvider aprov;

    @Override
    public KeyStore getKeystore(char[] password) throws  KeyStoreException {
        try {
            System.out.println("Tiene PKCS11: " + PKCS11_CONFIG);
            InputStream configStream = new ByteArrayInputStream(PKCS11_CONFIG);

            Provider sunPKCS11Provider = this.createSunPKCS11Provider(configStream);
            Security.addProvider(sunPKCS11Provider);

            KeyStore.Builder ksBuilder = KeyStore.Builder.newInstance(
                    "PKCS11",
                    null,
                    new KeyStore.CallbackHandlerProtection(new SimpleCallbackHandler(null, password))); //cmdLineHdlr

            KeyStore ks = ksBuilder.getKeyStore();

            aprov = (AuthProvider) Security.getProvider(sunPKCS11Provider.getName());
            aprov.setCallbackHandler(new SimpleCallbackHandler(null, password)); //cmdLineHdlr

            aprov.login(null, null);
//

            return ks;  //keyStore
//

        } catch (LoginException ex) {
            Logger.getLogger(WindowsJDK5KeyStoreProvider.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error en el loginExcep" + ex);
        } catch (KeyStoreException e) {
            System.out.println("e:" + e.getCause().toString().length());
            throw new KeyStoreException(e);
        }
//                catch (Exception e){
//                    System.out.println("Error en el keystore:"  + e.getCause().toString().length() );
//                    throw new KeyStoreException( e.getMessage() ,e.getCause());
//                }
        return null;
    }

    /**
     * <code> logout </code>
     *  Esta función permite limpiar de memoria el keystore.
     * @throws javax.security.auth.login.LoginException
     */
    public void logout() throws LoginException {

        this.aprov.logout();
    }

    /**
     * Instancia la clase <code>sun.security.pkcs11.SunPKCS11</code>
     * dinamicamente, usando Java Reflection.
     *
     * @return una instancia de <code>sun.security.pkcs11.SunPKCS11</code>
     */
    @SuppressWarnings("unchecked")
    private Provider createSunPKCS11Provider(InputStream configStream) throws ProviderException, KeyStoreException {
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

            throw new KeyStoreException(e.getMessage(), e.getCause());
        } catch (IllegalAccessException e) {
            throw new KeyStoreException(e);
        } catch (InstantiationException e) {
            throw new KeyStoreException(e);
        }
    }
}
