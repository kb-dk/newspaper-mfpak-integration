package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.ConfigurationProvider;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.TCKTestSuite;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/20/13
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class MfPakTCKTestSuite extends TCKTestSuite {

    private MfPakConfiguration configuration;

       @BeforeMethod(groups = {"integrationTest"})
       public void loadConfiguration() throws IOException {
           ConfigurationProvider configurationProvider = new ConfigurationProvider();
           configuration = configurationProvider.loadConfiguration();
       }


    @Override
    public boolean isRunNrInBatchID() {
        return false;
    }

    @Override
    public DataSource getDataSource() {
         return new MfPakDataSource(configuration);
     }

    @Override
    public String getValidBatchID() {
        return "4004";
    }

    @Override
    public String getInvalidBatchID() {
        return "4242";
    }

    @Override
    public String getValidAndSucessfullEventIDForValidBatch() {
        return "Shipped";
    }

    @Override
    public String getInvalidEventIDForValidBatch() {
        return "LostAtSea";
    }
}
