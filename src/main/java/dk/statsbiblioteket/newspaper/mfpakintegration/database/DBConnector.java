package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to initialise a connection pool from an MfPakConfiguration instance and obtain database connections
 * from it.
 */
public class DBConnector {
    /** The log.*/
    private Logger log = LoggerFactory.getLogger(getClass());
    /** The pool with data sources for the database connections.*/
    private ComboPooledDataSource connectionPool;

    /**
     * Constructor.
     * @param configuration The specifics for the configuration of the database.
     */
    public DBConnector(MfPakConfiguration configuration) {
        silenceC3P0Logger();
        this.connectionPool = new ComboPooledDataSource();
        initialiseConnectionPool(configuration);
    }

    /**
     * Initialises the ConnectionPool for the connections to the database.
     */
    private void initialiseConnectionPool(MfPakConfiguration configuration) {
        try {
            log.info("Creating the connection to the database '" + configuration + "'.");
            connectionPool.setDriverClass(configuration.getDatabaseDriver());
            connectionPool.setJdbcUrl(configuration.getDatabaseUrl());
            if(configuration.getDatabaseUser() != null) {
                connectionPool.setUser(configuration.getDatabaseUser());
            }
            if(configuration.getDatabasePassword() != null) {
                connectionPool.setPassword(configuration.getDatabasePassword());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not connect to the database '" + configuration + "'", e);
        }
    }

    /**
     * Hack to kill com.mchange.v2 log spamming.
     */
    private void silenceC3P0Logger() {
        Properties p = new Properties(System.getProperties());
        p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF"); // or any other
        System.setProperties(p);
    }

    /**
     * Creates and connects to the database.
     * @return The connection to the database.
     */
    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not establish connection to the database", e);
        }
    }

    /**
     * Cleans up after use.
     */
    public void destroy() {
        try {
            DataSources.destroy(connectionPool);
        } catch (SQLException e) {
            log.error("Could not clean up the database", e);
        }
    }
}
