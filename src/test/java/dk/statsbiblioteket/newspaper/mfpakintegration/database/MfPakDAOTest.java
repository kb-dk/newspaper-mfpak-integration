package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.Event;
import dk.statsbiblioteket.newspaper.mfpakintegration.EventID;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

public class MfPakDAOTest {

    private MfPakConfiguration configuration;

    @BeforeMethod(groups = {"integrationTest", "externalTest"})
    public void loadConfiguration() throws IOException {
        ConfigurationProvider configurationProvider = new ConfigurationProvider();
        configuration = configurationProvider.loadConfiguration();
    }

    /**
     * Test that we can get batches and events out of the database via the DAO. There should be at least fpour batches
     * in the database and at least one event each of types "Shipping" and "Received".
     * @throws Exception
     */
    @Test(groups = {"externalTest"})
    public void testGetAllBatches() throws Exception {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            List<Batch> batches = dao.getAllBatches();
            assertTrue("Should have at least four batches", batches.size() >= 4);
            int shipped = 0;
            int created = 0;
            for (Batch batch : batches) {
                for (Event event : batch.getEventList()) {
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
    }

    @Test(groups = {"externalTest"})
    public void testGetBatchByBarcode() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            Batch batch = dao.getBatchByBarcode("4004");
            assertNotNull("Should get non-null batch", batch);
            assertEquals("Batch should have three events.", 4, batch.getEventList().size());
        }
    }

    @Test(groups = {"externalTest"})
    public void testGetEvent() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            Event event = dao.getEvent("4002", "Initial");
            assertNotNull("Should have found this event.", event);
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetNewspaperID() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            String daoNewspaperID = dao.getNewspaperID("4001");
            assertTrue("boersen".equals(daoNewspaperID));
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetNewspaperEntity() throws SQLException, ParseException {
        String NEWSPAPER_ID = "adresseavisen1759";
        String EXPECTED_NEWSPAPER_TITLE = "Kiøbenhavns Kongelig alene priviligerede Adresse-Contoirs Efterretninger";
        String EXPECTED_PUBLICATION_LOCATION = "København";
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date validDate = df.parse("1800-03-03");
            NewspaperEntity entity = dao.getNewspaperEntity(NEWSPAPER_ID, validDate);
            assertNotNull("The newspaper entity should not be null", entity);
            assertEquals(NEWSPAPER_ID, entity.getNewspaperID());
            assertEquals(EXPECTED_NEWSPAPER_TITLE, entity.getNewspaperTitle());
            assertEquals(EXPECTED_PUBLICATION_LOCATION, entity.getPublicationLocation());
            assertTrue(validDate.after(entity.getNewspaperDateRange().getFromDate()));
            assertTrue(validDate.before(entity.getNewspaperDateRange().getToDate()));
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetNewspaperEntityBadDate() throws SQLException, ParseException {
        String NEWSPAPER_ID = "adresseavisen1759";
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date validDate = df.parse("1900-03-03");
            NewspaperEntity entity = dao.getNewspaperEntity(NEWSPAPER_ID, validDate);
            assertNull(entity);
        }
    }

    @Test(groups = {"externalTest"})
    public void testGetBatchDateRanges() throws SQLException, ParseException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            List<NewspaperDateRange> dateRanges = dao.getBatchDateRanges("400022028242");
            assertNotNull(dateRanges);
            assertEquals(4, dateRanges.size());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date firstFromDate = df.parse("1899-10-01");
            assertEquals(firstFromDate, dateRanges.get(0).getFromDate());
            Date secondFromDate = df.parse("1910-10-01");
            assertEquals(secondFromDate, dateRanges.get(1).getFromDate());
            Date thirdFromDate = df.parse("1920-10-01");
            assertEquals(thirdFromDate, dateRanges.get(2).getFromDate());
            Date fourthFromDate = df.parse("1971-10-01");
            assertEquals(fourthFromDate, dateRanges.get(3).getFromDate());
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetBatchDateRangesNoBatch() throws SQLException, ParseException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            String NON_EXISTING_BATCH_ID = "999999999999";
            List<NewspaperDateRange> dateRanges = dao.getBatchDateRanges(NON_EXISTING_BATCH_ID);
            assertNull("There should not be any date ranges for the non-existing batch", dateRanges);
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetBatchNewspaperTitles() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            List<NewspaperEntity> titles = dao.getBatchNewspaperEntities("400022028242");
            assertNotNull(titles);
            assertEquals(2, titles.size());
            assertEquals("Børsen", titles.get(0).getNewspaperTitle());
            assertEquals("Det nye Børsen", titles.get(1).getNewspaperTitle());
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetBatchNewspaperTitlesNoBatch() throws SQLException, ParseException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            String NON_EXISTING_BATCH_ID = "999999999999";
            List<NewspaperEntity> titles = dao.getBatchNewspaperEntities(NON_EXISTING_BATCH_ID);
            assertNull("There should not be any date ranges for the non-existing batch", titles);
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetBatchOptions() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            NewspaperBatchOptions options = dao.getBatchOptions("400022028242");
            assertNotNull(options);
            assertEquals(true, options.isOptionB1());
            assertEquals(true, options.isOptionB2());
            assertEquals(true, options.isOptionB3());
            assertEquals(true, options.isOptionB4());
            assertEquals(true, options.isOptionB5());
            assertEquals(true, options.isOptionB6());
            assertEquals(true, options.isOptionB7());
            assertEquals(true, options.isOptionB8());
            assertEquals(true, options.isOptionB9());
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testGetBatchOptionsNoBatch() throws SQLException, ParseException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            String NON_EXISTING_BATCH_ID = "999999999999";
            NewspaperBatchOptions options = dao.getBatchOptions(NON_EXISTING_BATCH_ID);
            assertNull("There should not be any options for the non-existing batch", options);
        }
    }

    @Test(groups = {"externalTest"})
    public void testGetBatchShipmentDate() throws SQLException, ParseException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            String EXISTING_BATCH_ID = "400022028241";
            Date shipmentDate = dao.getBatchShipmentDate(EXISTING_BATCH_ID);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date expectedDate = df.parse("2013-11-11");
            assertEquals(expectedDate, shipmentDate);
        }
    }

    @Test(groups = {"externalTest"})
    public void testGetBatchShipmentDateNoSuchBatch() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            String NON_EXISTING_BATCH_ID = "999999999999";
            Date shipmentDate = dao.getBatchShipmentDate(NON_EXISTING_BATCH_ID);
            assertNull(shipmentDate);
        }
    }

    @Test(groups = {"externalTest"})
    public void testGetBatchShipmentDateNonShippedBatch() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            String NON_SHIPPED_BATCH_ID = "400022028242";
            Date shipmentDate = dao.getBatchShipmentDate(NON_SHIPPED_BATCH_ID);
            assertNull(shipmentDate);
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testBatchTriggerInitialNotRecv() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            Collection<String> pastSuccessful = Arrays.asList(EventID.INITIAL.getFormal());
            Collection<String> futureEvents = Arrays.asList(EventID.RECEIVED_FROM_SUPPLIER.getFormal());
            Collection<Batch> batches = new HashSet<>();
            Batch b1 = new Batch();
            b1.setBatchID("1001");
            batches.add(b1);
            Batch b2 = new Batch();
            b2.setBatchID("1002");
            batches.add(b2);
            Batch b3 = new Batch();
            b3.setBatchID("1003");
            batches.add(b3);
            Batch b4 = new Batch();
            b4.setBatchID("1004");
            batches.add(b4);
            Set<String> expectedBatchIDs = new HashSet<>();
            expectedBatchIDs.add("1001");
            expectedBatchIDs.add("1002");
            expectedBatchIDs.add("1003");
            Iterator<Batch> mfpakBatches = dao.getTriggeredBatches(pastSuccessful, null, futureEvents, batches);
            assertTrue(mfpakBatches.hasNext());
            while (mfpakBatches.hasNext()) {
                Batch batch = mfpakBatches.next();
                assertTrue(expectedBatchIDs.contains(batch.getBatchID()));
            }
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testBatchTriggerAddedToShippingNotRecv() throws SQLException {
        try(MfPakDAO dao = new MfPakDAO(configuration)) {
            Collection<String> pastSuccessful = Arrays.asList(EventID.ADDED_TO_SHIPPING_CONTAINER.getFormal());
            Collection<String> futureEvents = Arrays.asList(EventID.RECEIVED_FROM_SUPPLIER.getFormal());
            Collection<Batch> batches = new HashSet<>();
            Batch b1 = new Batch();
            b1.setBatchID("1001");
            batches.add(b1);
            Batch b2 = new Batch();
            b2.setBatchID("1002");
            batches.add(b2);
            Batch b3 = new Batch();
            b3.setBatchID("1003");
            batches.add(b3);
            Batch b4 = new Batch();
            b4.setBatchID("1004");
            batches.add(b4);
            Set<String> expectedBatchIDs = new HashSet<>();
            expectedBatchIDs.add("1002");
            expectedBatchIDs.add("1003");
            Iterator<Batch> mfpakBatches = dao.getTriggeredBatches(pastSuccessful, null, futureEvents, batches);
            assertTrue(mfpakBatches.hasNext());
            while (mfpakBatches.hasNext()) {
                Batch batch = mfpakBatches.next();
                assertTrue(expectedBatchIDs.contains(batch.getBatchID()));
            }
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testBatchTriggerAllAddedToContainerButBatchIDLimited() throws SQLException {
        try(MfPakDAO dao = new MfPakDAO(configuration)) {
            Collection<String> pastSuccessful = Arrays.asList(EventID.ADDED_TO_SHIPPING_CONTAINER.getFormal());
            Collection<String> futureEvents = new ArrayList<>();
            Collection<Batch> batches = new HashSet<>();
            Batch b1 = new Batch();
            b1.setBatchID("1001");
            batches.add(b1);
            Batch b4 = new Batch();
            b4.setBatchID("1004");
            batches.add(b4);
            Set<String> expectedBatchIDs = new HashSet<>();
            expectedBatchIDs.add("1004");
            Iterator<Batch> mfpakBatches = dao.getTriggeredBatches(pastSuccessful, null, futureEvents, batches);
            assertTrue(mfpakBatches.hasNext());
            while (mfpakBatches.hasNext()) {
                Batch batch = mfpakBatches.next();
                assertTrue(expectedBatchIDs.contains(batch.getBatchID()));
            }
        }
    }
    
    @Test(groups = {"externalTest"})
    public void testBatchTriggerInitialAndAddedToShippingNotShippedOrRecv() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            Collection<String> pastSuccessful = Arrays.asList(EventID.INITIAL.getFormal(), EventID.ADDED_TO_SHIPPING_CONTAINER.getFormal());
            Collection<String> futureEvents = Arrays.asList(EventID.SHIPPED_TO_SUPPLIER.getFormal(), EventID.RECEIVED_FROM_SUPPLIER.getFormal());
            Collection<Batch> batches = new HashSet<>();
            Batch b1 = new Batch();
            b1.setBatchID("1001");
            batches.add(b1);
            Batch b2 = new Batch();
            b2.setBatchID("1002");
            batches.add(b2);
            Batch b3 = new Batch();
            b3.setBatchID("1003");
            batches.add(b3);
            Batch b4 = new Batch();
            b4.setBatchID("1004");
            batches.add(b4);
            Set<String> expectedBatchIDs = new HashSet<>();
            expectedBatchIDs.add("1002");
            Iterator<Batch> mfpakBatches = dao.getTriggeredBatches(pastSuccessful, null, futureEvents, batches);
            assertTrue(mfpakBatches.hasNext());
            while (mfpakBatches.hasNext()) {
                Batch batch = mfpakBatches.next();
                assertTrue(expectedBatchIDs.contains(batch.getBatchID()));
            }
        }

    }

    
    @Test(groups = {"externalTest"})
    public void testGetTriggeredBatchesWithEmptyBatchesLimitation() throws SQLException {
        try (MfPakDAO dao = new MfPakDAO(configuration)) {
            Collection<String> pastSuccessful = Arrays.asList(EventID.INITIAL.getFormal());
            Collection<String> futureEvents = Arrays.asList(EventID.SHIPPED_TO_SUPPLIER.getFormal());
            Collection<Batch> batches = new HashSet<>();
            Iterator<Batch> mfpakBatches = dao.getTriggeredBatches(pastSuccessful, null, futureEvents, batches);
            assertFalse(mfpakBatches.hasNext());
        }
    }
       
}
