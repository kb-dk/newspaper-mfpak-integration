package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.medieplatform.autonomous.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.medieplatform.autonomous.processmonitor.datasources.TCKTestSuite;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.ConfigurationProvider;
import dk.statsbiblioteket.util.Pair;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;

public class MfPakTCKTestSuite
        extends TCKTestSuite {

    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest"})
    public void loadConfiguration()
            throws
            IOException {
        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        configuration = configurationProvider.loadConfiguration();
    }

    @Override
    public DataSource getDataSource() {
        return new MfPakDataSource(configuration);
    }

    @Override
    public Pair<String, Integer> getValidBatchID() {
        return new Pair<>("4004", 1);
    }

    @Override
    public Pair<String, Integer> getInvalidBatchID() {
        return new Pair<>("4242", null);
    }

    @Override
    public String getValidAndSucessfullEventIDForValidBatch() {
        return "Shipped_to_supplier";
    }
}
