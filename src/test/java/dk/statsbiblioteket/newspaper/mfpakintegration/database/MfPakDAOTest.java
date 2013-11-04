package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.Event;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;

public class MfPakDAOTest {

    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest"})
    public void loadConfiguration() throws IOException {
        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        configuration = configurationProvider.loadConfiguration();
    }

    /**
     * Test that we can get batches and events out of the database via the DAO. There should be at least fpour batches
     * in the database and at least one event each of types "Shipping" and "Received".
     * @throws Exception
     */
    @Test(groups = {"integrationTest"})
    public void testGetAllBatches() throws Exception {
        MfPakDAO dao = new MfPakDAO(configuration);
        List<Batch> batches = dao.getAllBatches();
        
        assertTrue("Should have at least four batches", batches.size() >= 4);
        int shipped = 0;
        int created = 0;
        for (Batch batch: batches) {
            for (Event event: batch.getEventList() ) {
                if (event.getEventID().equals("Shipped_to_supplier")) {
                    shipped++;
                } else if (event.getEventID().equals("Initial")) {
                    created++;
                }
            }
        }
        assertTrue("Should have at least one Shipping event", shipped >= 1);
        assertTrue("Should have at least one Received event", created >= 1);
    }

    @Test(groups = {"integrationTest"})
    public void testGetBatchByBarcode() throws SQLException {
        MfPakDAO dao = new MfPakDAO(configuration);
        Batch batch = dao.getBatchByBarcode("4004");
        assertNotNull("Should get non-null batch", batch);
        assertEquals("Batch should have three events.", 4, batch.getEventList().size());
    }

    @Test(groups = {"integrationTest"})
    public void testGetEvent() throws SQLException {
        MfPakDAO dao = new MfPakDAO(configuration);
        Event event = dao.getEvent("4002", "Initial");
        assertNotNull("Should have found this event.", event);
    }
    
    @Test(groups = {"integrationTest"})
    public void testGetNewspaperID() throws SQLException {
        MfPakDAO dao = new MfPakDAO(configuration);
        String daoNewspaperID = dao.getNewspaperID("4001");
        assertTrue("boersen".equals(daoNewspaperID));
    }
    
    @Test(groups = {"integrationTest"})
    public void testGetNewspaperEntity() throws SQLException, ParseException {
        String NEWSPAPER_ID = "adresseavisen1759";
        String EXPECTED_NEWSPAPER_TITLE = "Kiøbenhavns Kongelig alene priviligerede Adresse-Contoirs Efterretninger";
        String EXPECTED_PUBLICATION_LOCATION = "København";
        MfPakDAO dao = new MfPakDAO(configuration);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date validDate = df.parse("1800-03-03");
        
        NewspaperEntity entity = dao.getNewspaperEntity(NEWSPAPER_ID, validDate);
        assertNotNull("The newspaper entity should not be null", entity);
        assertEquals(NEWSPAPER_ID, entity.getNewspaperID());
        assertEquals(EXPECTED_NEWSPAPER_TITLE, entity.getNewspaperTitle());
        assertEquals(EXPECTED_PUBLICATION_LOCATION, entity.getPublicationLocation());
        assertTrue(validDate.after(entity.getStartDate()));
        assertTrue(validDate.before(entity.getEndDate()));
    }
    
    @Test(groups = {"integrationTest"})
    public void testGetNewspaperEntityBadDate() throws SQLException, ParseException {
        String NEWSPAPER_ID = "adresseavisen1759";
        MfPakDAO dao = new MfPakDAO(configuration);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date validDate = df.parse("1900-03-03");
        
        NewspaperEntity entity = dao.getNewspaperEntity(NEWSPAPER_ID, validDate);
        assertNull(entity);
    }

    @Test(groups = {"integrationTest"})
    public void testgetBatchDateRanges() throws SQLException, ParseException {
        MfPakDAO dao = new MfPakDAO(configuration);
        List<NewspaperDateRange> dateRanges = dao.getBatchDateRanges("400022028245");
        assertNotNull(dateRanges);
        assertEquals(3, dateRanges.size());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date firstFromDate = df.parse("1899-10-01");
        assertEquals(firstFromDate, dateRanges.get(0).getFromDate()); 
        Date secondFromDate = df.parse("1910-10-01");
        assertEquals(secondFromDate, dateRanges.get(1).getFromDate()); 
        Date thirdFromDate = df.parse("1920-10-01");
        assertEquals(thirdFromDate, dateRanges.get(2).getFromDate()); 
    }
    
    @Test(groups = {"integrationTest"})
    public void testgetBatchDateRangesNoBatch() throws SQLException, ParseException {
        MfPakDAO dao = new MfPakDAO(configuration);
        String NON_EXISTING_BATCH_ID = "999999999999";
        try {
        List<NewspaperDateRange> dateRanges = dao.getBatchDateRanges(NON_EXISTING_BATCH_ID);
        assertTrue(false); // Where is assertFail()??
        } catch (InconsistentDatabaseException e) {
            assertNotNull(e); //Yay we got the expected exception..
        }
        
    }
}
