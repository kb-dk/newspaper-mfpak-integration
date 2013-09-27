package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.EventID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private synchronized Connection getConnection() {
        if (connector == null) {
            connector = new DBConnector(configuration);
        }
        return connector.getConnection();
    }

    /**
     * Returns a list of all batches with their associated events from mfpak.
     *
     * @return The list of batches.
     */
    public List<Batch> getAllBatches() throws SQLException {
        Map<String, Batch> batchesById = new HashMap<String, Batch>();
        String getAllBarcodes = "select  batchId, rowId from batch";
        try (Connection con = getConnection(); Statement statement = con.createStatement()) {
            try (ResultSet rs = statement.executeQuery(getAllBarcodes)) {
                while (rs.next()) {
                    int barcode = rs.getInt("batchId");
                    Batch batch = new Batch();
                    batch.setBatchID(new Long(barcode));
                    batch.setEventList(new ArrayList<Event>());
                    batchesById.put(rs.getString("rowId"), batch);
                }
            }
            String getAllEvents = "SELECT batchrowId, name, batchstatus.created from batchstatus, status where statusrowId = status.rowId ";
            try (ResultSet rs = statement.executeQuery(getAllEvents)) {
                while (rs.next()) {
                    String batchId = rs.getString("batchrowId");
                    Timestamp createdTimestamp = rs.getTimestamp("created");  //We expect to add this to the API at some later point in time.
                    String status = rs.getString("name");
                    Batch batch = batchesById.get(batchId);
                    if (batch != null) {
                        try {
                            batch.getEventList().add(createEvent(status));
                        } catch (IllegalArgumentException e) {
                            log.warn(e.getMessage());
                        }
                    } else {
                        log.warn("Found an event '" + status + "' attached to an unknown batch with id '" + batchId);
                    }
                }
            }
        }

        return new ArrayList<Batch>(batchesById.values());
    }

    /**
     * Returns a batch with the given id (barcode) and all its relevant statuses, or null if no such batch exists.
     *
     * @param barcode
     * @return the batch.
     */
    public Batch getBatchByBarcode(int barcode) throws SQLException {
        try (Connection con = getConnection(); Statement stmt = con.createStatement()) {
            String getBatchId = "select rowId FROM batch WHERE batchId='" + barcode + "'";
            try (ResultSet rs = stmt.executeQuery(getBatchId)) {
                boolean batchExists = rs.next();
                if (!batchExists) {
                    log.warn("No such batch found: '" + barcode + "'");
                    return null;
                } else {
                    Batch batch = new Batch();
                    batch.setBatchID((long) barcode);
                    String id = rs.getString("rowId");
                    batch.setEventList(new ArrayList<Event>());
                    String getEvents = "SELECT name, batchstatus.created from batchstatus, status WHERE batchstatus.statusrowId=status.rowId AND batchstatus.batchrowId='" + id + "'";
                    try (Statement stmt2 = con.createStatement()) {
                        try (ResultSet rs2 = stmt2.executeQuery(getEvents)) {
                            while (rs2.next()) {
                                String status = rs2.getString("name");
                                Timestamp created = rs2.getTimestamp("created");
                                try {
                                    batch.getEventList().add(createEvent(status));
                                } catch (IllegalArgumentException e) {
                                    log.warn(e.getMessage());
                                }
                            }
                        }
                    }
                    if (rs.next()) {
                        log.warn("Found more than one batch with the barcode '" + barcode + "'");
                    }
                    return batch;
                }
            }
        }
    }

    /**
     * Creates a event object based on the status in the MfPak DB.
     *
     * @param status The name in the status table for this batch.
     * @return The corresponding event object.
     */
    private static Event createEvent(String status) {
        Event event = new Event();
        event.setSuccess(true);
        switch (status) {
            case "Initial":
                event.setEventID(EventID.Initial);
                break;
            case "Batch added to shipping container":
                event.setEventID(EventID.Added_to_shipping_container);
                break;
            case "Batch shipped to supplier":
                event.setEventID(EventID.Shipped_to_supplier);
                break;
            case "Batch shipped from supplier":
                event.setEventID(EventID.Shipped_from_supplier);
                break;
            case "Batch received from supplier":
                event.setEventID(EventID.Received_from_supplier);
                break;
            case "Batch follow-up":
                event.setEventID(EventID.FollowUp);
                break;
            case "Batch approved":
                event.setEventID(EventID.Approved);
                break;
            default:
                throw new IllegalArgumentException("Unknown batch status " + status);
        }
        return event;
    }

    /**
     * Returns a specific event specified by the barcode and eventStatus, or null if no such event has occurred.
     *
     * @param batchBarcode
     * @param eventStatus
     * @return the event.
     */
    public Event getEvent(int batchBarcode, EventID eventStatus) throws SQLException {
        Batch batch = getBatchByBarcode(batchBarcode);
        for (Event event : batch.getEventList()) {
            if (event.getEventID().equals(eventStatus)) {
                return event;
            }
        }
        return null;
    }
}
