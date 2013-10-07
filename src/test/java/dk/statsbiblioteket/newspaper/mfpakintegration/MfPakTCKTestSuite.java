package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.ConfigurationProvider;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.EventID;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.TCKTestSuite;
import dk.statsbiblioteket.util.Pair;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;

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
    public Pair<Long,Integer> getValidBatchID() {
        return new Pair<>(4004L,1);
    }

    @Override
    public Pair<Long,Integer> getInvalidBatchID() {
        return new Pair<Long, Integer>(4242L,null);
    }

    @Override
    public EventID getValidAndSucessfullEventIDForValidBatch() {
        return EventID.Shipped_to_supplier;
    }
}
