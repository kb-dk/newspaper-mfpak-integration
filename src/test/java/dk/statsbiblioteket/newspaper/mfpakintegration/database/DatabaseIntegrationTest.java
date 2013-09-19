package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DatabaseIntegrationTest {
    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest"})
    public void loadConfiguration() throws IOException {
        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        configuration = configurationProvider.loadConfiguration();
    }

    @Test(groups = {"integrationTest"})
    public void databaseConnectionTest() throws SQLException {
        MfPakDAO dao = new MfPakDAO(configuration);
        List<Batch> batches = dao.getAllBatches();
    }
}
