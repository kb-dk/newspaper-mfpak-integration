package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all calls to the actual database backend.
 */
public class MfPakDAO {
    private Logger log = LoggerFactory.getLogger(getClass());
    protected DBConnector connector = null;
    private final MfPakConfiguration configuration;

    public MfPakDAO(MfPakConfiguration configuration) {
        this.configuration = configuration;
        try {
            Class.forName(configuration.getDatabaseDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find database driver '" + configuration.getDatabaseDriver() +
                    "' in classpath.", e);
        }
    }

    private Connection getConnection() {
        if(connector == null) {
            connector = new DBConnector(configuration);
        }
        return connector.getConnection();
    }

    /**
     * Returns a list of all batches with their associated events from mfpak.
     * @return  The list of batches.
     */
    public List<Batch> getAllBatches() throws SQLException {
        Map<String, Batch> batchesById = new HashMap<String, Batch>();
        Connection con = getConnection();
        String getAllBarcodes = "select  batchId, rowId from batch";
        final Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(getAllBarcodes);
        while (rs.next()) {
            String barcode = rs.getString("batchId");
            Batch batch = new Batch();
            batch.setBatchID(barcode);
            batch.setEventList(new ArrayList<Event>());
            batchesById.put(rs.getString("rowId"), batch);
        }
        String getAllEvents = "SELECT batchrowId, name, batchstatus.created from batchstatus, status where statusrowId = status.rowId ";
        rs = statement.executeQuery(getAllEvents);
        while (rs.next()) {
            String batchId = rs.getString("batchrowId");
            Timestamp createdTimestamp = rs.getTimestamp("created");  //We expect to add this to the API at some later point in time.
            String status = rs.getString("name");
            Event event = new Event();
            Batch batch = batchesById.get(batchId);
            if (batch != null) {
               if ("Initial".equals(status)) {
                   event.setEventID("Created");
                   event.setSuccess(true);
                   batch.getEventList().add(event);
               } else if ("Batch shipped to supplier".equals(status)) {
                   event.setEventID("Shipped");
                   event.setSuccess(true);
                   batch.getEventList().add(event);
               }  else {
                   log.debug("Ignoring an event of type '" + status + "'");
               }
            } else {
                log.warn("Found an event '" + status + "' attached to an unknown batch with id '" + batchId);
            }
        }
        statement.close();
        return new ArrayList<Batch>(batchesById.values());
    }

    /**
     * Returns a batch with the given id (barcode) and all its relevant statuses, or null if no such batch exists.
     * @param barcode
     * @return the batch.
     */
    public Batch getBatchByBarcode(int barcode) throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        String getBatchId = "select  rowId FROM batch WHERE batchId='"+barcode+"'";
        ResultSet rs = stmt.executeQuery(getBatchId);
        boolean batchExists = rs.next();
        if (!batchExists) {
            log.warn("No such batch found: '" + barcode + "'");
            stmt.close();
            return null;
        } else {
            Batch batch = new Batch();
            batch.setBatchID(""+barcode);
            String id = rs.getString("rowId");
            batch.setEventList(new ArrayList<Event>());
            String getEvents = "SELECT name, batchstatus.created from batchstatus, status WHERE batchstatus.statusrowId=status.rowId AND batchstatus.batchrowId='" + id + "'";
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery(getEvents);
            while (rs2.next()) {
                Event event = new Event();
                String status = rs2.getString("name");
                Timestamp created = rs2.getTimestamp("created");
                if ("Initial".equals(status)) {
                   event.setEventID("Created");
                   event.setSuccess(true);
                   batch.getEventList().add(event);
               } else if ("Batch shipped to supplier".equals(status)) {
                   event.setEventID("Shipped");
                   event.setSuccess(true);
                   batch.getEventList().add(event);
               }  else {
                   log.debug("Ignoring an event of type '" + status + "'");
               }
            }
            stmt2.close();
            if (rs.next()) {
                log.warn("Found more than one batch with the barcode '" + barcode + "'");
            }
            stmt.close();
            return batch;
        }
    }

    /**
     * Returns a specific event specified by the barcode and eventStatus, or null if no such event has occurred.
     * @param batchBarcode
     * @param eventStatus
     * @return the event.
     */
    public Event getEvent(int batchBarcode, String eventStatus) throws SQLException {
        Batch batch = getBatchByBarcode(batchBarcode);
        for (Event event: batch.getEventList()) {
            if (event.getEventID().equals(eventStatus)) {
                return event;
            }
        }
        return null;
    }
}
