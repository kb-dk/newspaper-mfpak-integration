package dk.statsbiblioteket.newspaper.mfpakintegration.configuration;

/**
 * Models the configurable properties of the MKPak component.
 */
public class MfPakConfiguration {
    private String databaseDriver = "org.postgresql.Driver";
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;

    public String getDatabaseUrl() {
        return databaseUrl;
    }
    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }
    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }
    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public String getDatabaseDriver() {
        return databaseDriver;
    }

    @Override
    public String toString() {
        return "MfPakConfiguration{" +
                "databaseDriver='" + databaseDriver + '\'' +
                ", databaseUrl='" + databaseUrl + '\'' +
                ", databaseUser='" + databaseUser + '\'' +
                '}';
    }
}
