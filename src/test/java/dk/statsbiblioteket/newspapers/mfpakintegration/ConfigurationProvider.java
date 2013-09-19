package dk.statsbiblioteket.newspapers.mfpakintegration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.ConfigurationProperties;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class  ConfigurationProvider {
    private Logger log = LoggerFactory.getLogger(getClass());
    private final Properties properties = new Properties();

    public MfPakConfiguration loadConfiguration() throws IOException {
        String pathToProperties = System.getProperty("integration.test.newspaper.properties");
        if (pathToProperties == null)  {
            throw new IllegalStateException("Environment variable 'integration.test.newspaper.properties' is " +
                    "not defined, configuration can not be loaded");
        }
        log.info("Loading configuration from: " + pathToProperties);
        properties.load(new FileInputStream(pathToProperties));
        return  createConfiguration(properties);
    }

    private MfPakConfiguration createConfiguration (Properties properties) {
        MfPakConfiguration configuration = new MfPakConfiguration();

        configuration.setDatabaseUrl(properties.getProperty(ConfigurationProperties.DATABASE_URL));
        assert (configuration.getDatabaseUrl() != null);

        configuration.setDatabaseUser(properties.getProperty(ConfigurationProperties.DATABASE_USER));
        assert (configuration.getDatabaseUser() != null);

        configuration.setDatabasePassword(properties.getProperty(ConfigurationProperties.DATABASE_PASSWORD));
        assert (configuration.getDatabasePassword() != null);

        return configuration;
    }
}
