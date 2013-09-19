package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import dk.statsbiblioteket.newspaper.mfpakintegration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/18/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MfPakDAOTest {

    /**
     * Test that we can get batches and events out of the database via the DAO. There should be at least fpour batches
     * in the database and at least one event each of types "Shipping" and "Received".
     * @throws Exception
     */
    @Test
    public void testGetAllBatches() throws Exception {
        MfPakConfiguration config = new MfPakConfiguration();
        config.setDatabaseUrl("jdbc:postgresql://achernar/mfpak-devel");
        config.setDatabaseUser("mfpak");
        config.setDatabasePassword("JJ1KM0bvVf");
        MfPakDAO dao = new MfPakDAO(config);
        List<Batch> batches = dao.getAllBatches();
        assertTrue("Should have at least four batches", batches.size() > 3);
        int shipping = 0;
        int received = 0;
        for (Batch batch: batches) {
            for (Event event: batch.getEventList() ) {
                if (event.getEventID().equals("Shipping")) {
                    shipping++;
                } else if (event.getEventID().equals("Received")) {
                    received++;
                } else {
                    throw new RuntimeException("Unknown event type " + event.getEventID());
                }
            }
        }
        assertTrue("Should have at least one Shipping event", shipping > 0);
        assertTrue("Should have at least one Received event", received > 0);
    }

    @Test
    public void testGetBatchByBarcode() throws SQLException {
        MfPakConfiguration config = new MfPakConfiguration();
        config.setDatabaseUrl("jdbc:postgresql://achernar/mfpak-devel");
        config.setDatabaseUser("mfpak");
        config.setDatabasePassword("JJ1KM0bvVf");
        MfPakDAO dao = new MfPakDAO(config);
        Batch batch = dao.getBatchByBarcode("4004");
        assertNotNull("Should get non-null batch", batch);
        assertEquals("Batch should have two events.", 2, batch.getEventList().size());
    }

    @Test
    public void testGetEvent() throws SQLException {
        MfPakConfiguration config = new MfPakConfiguration();
        config.setDatabaseUrl("jdbc:postgresql://achernar/mfpak-devel");
        config.setDatabaseUser("mfpak");
        config.setDatabasePassword("JJ1KM0bvVf");
        MfPakDAO dao = new MfPakDAO(config);
        Event event = dao.getEvent("4003", "Received");
        assertNotNull("Should have found this event.", event);
    }
}
