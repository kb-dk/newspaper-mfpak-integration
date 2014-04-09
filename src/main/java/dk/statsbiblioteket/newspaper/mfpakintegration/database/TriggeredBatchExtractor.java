package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.Event;
import dk.statsbiblioteket.newspaper.mfpakintegration.EventID;

/**
 * Class to encapsulate the intristics of extracting triggered batches from the MFPAK database 
 */
public class TriggeredBatchExtractor {

    private static Logger log = LoggerFactory.getLogger(TriggeredBatchExtractor.class);

    /** Connection to get the data from */
    Connection connection;
    
    TriggeredBatchExtractor(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Do the actual work of extracting the events. 
     * This involves building the conditionals for the SQL query, executing it and building the result iterator.  
     */
    Iterator<Batch> getTriggeredBatches(Collection<String> pastSuccessfulEvents,
            Collection<String> futureEvents, Collection<Batch> batches) throws SQLException {

        Map<String, Batch> mfpakBatches = new HashMap<>();
        
        String selectSql = buildSql(pastSuccessfulEvents, futureEvents, batches);
        log.debug("Extracting triggered batches with sql '{}'", selectSql);
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            addParameters(stmt, pastSuccessfulEvents, futureEvents, batches);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Batch currentBatch = null;
                    Long batchID = rs.getLong("batchid");
                    if(!mfpakBatches.containsKey(batchID.toString())) {
                        currentBatch = new Batch();
                        currentBatch.setRoundTripNumber(0);
                        currentBatch.setBatchID(batchID.toString());
                        currentBatch.setEventList(new ArrayList<Event>());
                        mfpakBatches.put(batchID.toString(), currentBatch);
                    } else {
                        currentBatch = mfpakBatches.get(batchID.toString());
                    }
                    
                    Timestamp createdTimestamp = rs.getTimestamp("created");
                    String status = rs.getString("name");
                    currentBatch.getEventList().add(MfPakDAO.createEvent(status, createdTimestamp));
                }
        
            }
        }
        
        return mfpakBatches.values().iterator();
    }
    
    private String buildSql(Collection<String> pastSuccessfulEvents, Collection<String> futureEvents, 
            Collection<Batch> batches) {
        final StringBuilder selectSql = new StringBuilder("SELECT batchid, name, batchstatus.created FROM Batch" 
                + " JOIN batchstatus ON batch.rowid = batchstatus.batchrowid"
                + " JOIN status ON batchstatus.statusrowid = status.rowid");
        final String inclusiveNameSql = " batchid IN (SELECT batchid FROM Batch" 
                + " JOIN batchstatus ON batch.rowid = batchstatus.batchrowid"
                + " JOIN status ON batchstatus.statusrowid = status.rowid"
                + " WHERE name = ? )";
        final String exclusiveNamesSql = " batchid NOT IN (SELECT batchid FROM Batch" 
                + " JOIN batchstatus ON batch.rowid = batchstatus.batchrowid"
                + " JOIN status ON batchstatus.statusrowid = status.rowid"
                + " WHERE name IN ( #### ))";
        final String batchLimitSql = " batchid IN ( #### )";
        
        boolean haveLimit = false;
        
        if(pastSuccessfulEvents != null && !pastSuccessfulEvents.isEmpty()) {
            for(String event : pastSuccessfulEvents) {
                appendLimit(selectSql, haveLimit, inclusiveNameSql);
                haveLimit = true;
            }
        }
        
        if(futureEvents != null && !futureEvents.isEmpty()) {
            String placeholders = buildPlaceholdersString(futureEvents.size());
            appendLimit(selectSql, haveLimit, exclusiveNamesSql.replace("####", placeholders));
            haveLimit = true;
        }
        
        if(batches != null) {
            String placeholders = buildPlaceholdersString(batches.size());
            appendLimit(selectSql, haveLimit, batchLimitSql.replace("####", placeholders));
            haveLimit = true;
        }
        
        return selectSql.toString();
    }
    
    private void addParameters(PreparedStatement stmt, Collection<String> pastSuccessfulEvents, 
            Collection<String> futureEvents, Collection<Batch> batches) throws SQLException {
        int i = 1;
        if(pastSuccessfulEvents != null && !pastSuccessfulEvents.isEmpty()) {
            for(String event : pastSuccessfulEvents) {
                stmt.setString(i, EventID.fromFormal(event).getMfpak());
                i++;
            }
        }
        
        if(futureEvents != null && !futureEvents.isEmpty()) {
            for(String event : futureEvents) {
                stmt.setString(i, EventID.fromFormal(event).getMfpak());
                i++;
            }
        }
        
        if(batches != null) {
            for(Batch b : batches) {
                stmt.setLong(i, Long.parseLong(b.getBatchID()));
                i++;
            }
        }
    }
    
    private void appendLimit(StringBuilder sql, boolean firstLimit, String limitSql) {
        if(!firstLimit) {
            sql.append(" WHERE");
        } else {
            sql.append(" AND");
        }
        sql.append(limitSql);
    }

    private static String buildPlaceholdersString(int numberOfPlaceholders) {
        StringBuilder placeholders = new StringBuilder("");
        for(int i = 0; i<numberOfPlaceholders; i++) {
            if(i == 0) {
                placeholders.append("?");
            } else {
                placeholders.append(", ?");
            }
        }
        return placeholders.toString();
    }
}
