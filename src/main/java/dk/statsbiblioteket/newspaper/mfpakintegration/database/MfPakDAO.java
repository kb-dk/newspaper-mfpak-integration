package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.sql.Connection;

import dk.statsbiblioteket.newspaper.mfpakintegration.MfPakConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all calls to the actual database backend.
 */
public class MfPakDAO {
    private Logger log = LoggerFactory.getLogger(getClass());
    protected DBConnector connector = null;
    private final MfPakConfiguration configuration;

    public MfPakDAO(MfPakConfiguration configuration) {
        this.configuration = configuration;
    }

    private Connection getConnection() {
        if(connector == null) {
            connector = new DBConnector(configuration);
        }
        return connector.getConnection();
    }
}
