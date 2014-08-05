package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import dk.statsbiblioteket.medieplatform.autonomous.ConfigConstants;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Enables loading of the configuration from external folder to emulate configuration injection.
 */
public class  ConfigurationProvider {
    private static Logger log = LoggerFactory.getLogger(ConfigurationProvider.class);

    public MfPakConfiguration loadConfiguration() throws IOException {
        Properties properties = loadProperties();
        return  createConfiguration(properties);
    }

    public static Properties loadProperties() throws IOException {
        String pathToProperties = System.getProperty("integration.test.newspaper.properties");

        if (pathToProperties == null)  {
            throw new IllegalStateException("Environment variable 'integration.test.newspaper.properties' is " +
                    "not defined, configuration can not be loaded");
        }
        log.info("Loading configuration from: " + pathToProperties);
        Properties properties = new Properties();
        properties.load(new FileInputStream(pathToProperties));
        return properties;
    }

    private MfPakConfiguration createConfiguration (Properties properties) {
        MfPakConfiguration configuration = new MfPakConfiguration();

        configuration.setDatabaseUrl(properties.getProperty(ConfigConstants.MFPAK_URL));
        assert (configuration.getDatabaseUrl() != null);

        configuration.setDatabaseUser(properties.getProperty(ConfigConstants.MFPAK_USER));
        assert (configuration.getDatabaseUser() != null);

        configuration.setDatabasePassword(properties.getProperty(ConfigConstants.MFPAK_PASSWORD));
        assert (configuration.getDatabasePassword() != null);

        return configuration;
    }
}
