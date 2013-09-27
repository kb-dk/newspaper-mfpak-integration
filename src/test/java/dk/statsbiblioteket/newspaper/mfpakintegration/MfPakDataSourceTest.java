package dk.statsbiblioteket.newspaper.mfpakintegration;


import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.ConfigurationProvider;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.EventID;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotFoundException;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotWorkingProperlyException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MfPakDataSourceTest {
    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest"})
    public void loadConfiguration() throws IOException {
        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        configuration = configurationProvider.loadConfiguration();
    }

    @Test(groups = {"integrationTest"})
    public void testGetBatches() throws Exception {
        MfPakDataSource source = new MfPakDataSource(configuration);
        List<Batch> batches = source.getBatches(true, null);
        assertTrue("Should have at least four batches", batches.size() >= 4);
        int created = 0;
        int shipped = 0;
        for (Batch batch: batches) {
            for (Event event: batch.getEventList() ) {
                if (event.getEventID().equals(EventID.Initial)) {
                    created++;
                } else if (event.getEventID().equals(EventID.Shipped_to_supplier)) {
                    shipped++;
                }
            }
        }
        assertTrue("Should have at least one Shipping event", created >= 1);
        assertTrue("Should have at least one Received event", shipped >= 1);
    }

    @Test(groups = {"integrationTest"}, expectedExceptions = NotWorkingProperlyException.class)
    public void testGetBatchesWrongCredentials() throws Exception {
        configuration.setDatabasePassword("foobar");
        MfPakDataSource source = new MfPakDataSource(configuration);
        List<Batch> batches = source.getBatches(true, null);
    }

    @Test(groups = {"integrationTest"}, expectedExceptions = NotFoundException.class)
    public void testGetBatch() throws Exception {
        MfPakDataSource source = new MfPakDataSource(configuration);
        source.getBatch(1999L, true);
    }
    @Test(groups = {"integrationTest"}, expectedExceptions = NotFoundException.class)
    public void testGetBatchEvent() throws Exception {
        MfPakDataSource source = new MfPakDataSource(configuration);
        source.getBatchEvent(4001L, EventID.Initial, false);
    }
}
