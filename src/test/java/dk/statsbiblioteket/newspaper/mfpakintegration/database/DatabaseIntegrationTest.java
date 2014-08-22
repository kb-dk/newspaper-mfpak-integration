package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DatabaseIntegrationTest {
    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest"})
    public void loadConfiguration() throws IOException {
        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        configuration = configurationProvider.loadConfiguration();
    }

    @Test(groups = {"integrationTest"})
    public void databaseConnectionTest() throws SQLException {
        try(MfPakDAO dao = new MfPakDAO(configuration)) {
            List<Batch> batches = dao.getAllBatches();
        }
    }
}
