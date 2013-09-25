package dk.statsbiblioteket.newspaper.mfpakintegration;

import java.io.IOException;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.ConfigurationProvider;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.EventID;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.TCKTestSuite;
import org.testng.annotations.BeforeMethod;

public class MfPakTCKTestSuite extends TCKTestSuite {

    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest"})
       public void loadConfiguration() throws IOException {
           ConfigurationProvider configurationProvider = new ConfigurationProvider();
           configuration = configurationProvider.loadConfiguration();
       }

    @Override
    public DataSource getDataSource() {
         return new MfPakDataSource(configuration);
     }

    @Override
    public Long getValidBatchID() {
        return 4004L;
    }

    @Override
    public Long getInvalidBatchID() {
        return 4242L;
    }

    @Override
    public EventID getValidAndSucessfullEventIDForValidBatch() {
        return EventID.Shipped_to_supplier;
    }
}
