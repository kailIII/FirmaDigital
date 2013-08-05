package ec.gov.informatica.firmadigital.keystore;

import java.util.logging.Logger;

/**
 * Obtiene la implementacion correcta de KeyStoreProvider de acuerdo al sistema
 * operativo.
 */
public class KeyStoreProviderFactory {

	private static final Logger log = Logger.getLogger(KeyStoreProviderFactory.class.getName());

	/**
	 * Obtiene la implementacion correcta de KeyStoreProvider de acuerdo al
	 * sistema operativo.
	 * 
	 * @return implementacion de KeyStoreProvider
	 */
	public static KeyStoreProvider createKeyStoreProvider() {
		String osName = System.getProperty("os.name");
		String javaVersion = System.getProperty("java.version");

		log.finer("Operating System:" + osName);
		log.finer("Java Version:" + javaVersion);

		if (osName.toUpperCase().indexOf("WINDOWS") == 0) {
			if (javaVersion.indexOf("1.6") == 0) {
				return new WindowsJDK6KeyStoreProvider();
			} else {
				return new WindowsJDK5KeyStoreProvider();
			}
		} else if (osName.toUpperCase().indexOf("LINUX") == 0) {
			return new LinuxKeyStoreProvider();
		} else if (osName.toUpperCase().indexOf("MAC") == 0) {
			return new AppleKeyStoreProvider();
		} else {
			throw new IllegalArgumentException("Sistema operativo no soportado!");
		}
	}
}