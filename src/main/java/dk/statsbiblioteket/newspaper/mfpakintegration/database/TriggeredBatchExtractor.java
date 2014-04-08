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
import java.util.List;
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
        List<String> argList = new ArrayList<>();
        
        String selectSql = buildSql(pastSuccessfulEvents, futureEvents, batches, argList);
        log.debug("Extracting triggered batches with sql '{}' and parameters '{}'", selectSql, argList);
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            for(int i = 0; i<argList.size(); i++) {
                stmt.setString(i+1, argList.get(i));
            }
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
            Collection<Batch> batches, List<String> argList) {
        final StringBuilder selectSql = new StringBuilder("SELECT batchid, name, batchstatus.created FROM Batch" 
                + " JOIN batchstatus ON batch.rowid = batchstatus.batchrowid"
                + " JOIN status ON batchstatus.statusrowid = status.rowid");
        final String inclusiveNamesSql = " name IN (?)";
        final String exclusiveNamesSql = " batchid NOT IN (SELECT batchid FROM Batch" 
                + " JOIN batchstatus ON batch.rowid = batchstatus.batchrowid"
                + " JOIN status ON batchstatus.statusrowid = status.rowid"
                + " WHERE name IN ( ? ))";
        final String batchLimitSql = " batchid IN (?)";
        
        boolean haveLimit = false;
        
        if(pastSuccessfulEvents != null && !pastSuccessfulEvents.isEmpty()) {
            appendLimit(selectSql, haveLimit, inclusiveNamesSql);
            haveLimit = true;
            argList.add(collectionToCommaSeparatedList(pastSuccessfulEvents));
        }
        
        if(futureEvents != null && !futureEvents.isEmpty()) {
            appendLimit(selectSql, haveLimit, exclusiveNamesSql);
            haveLimit = true;
            argList.add(collectionToCommaSeparatedList(futureEvents));
        }
        
        if(batches != null) {
            appendLimit(selectSql, haveLimit, batchLimitSql);
            haveLimit = true;
            argList.add(batchesToCommaSeparatedList(batches));
        }
        
        return selectSql.toString();
    }
    
    private void appendLimit(StringBuilder sql, boolean firstLimit, String limitSql) {
        if(!firstLimit) {
            sql.append(" WHERE");
        } else {
            sql.append(" AND");
        }
        sql.append(limitSql);
    }
    
    private static String collectionToCommaSeparatedList(Collection<String> collection) {
        StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        for(String s : collection) {
            if(!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append("'").append(EventID.fromFormal(s).getMfpak()).append("'");
        }
        
        return sb.toString();
    }
    
    private static String batchesToCommaSeparatedList(Collection<Batch> batches) {
        StringBuilder sb = new StringBuilder();
        
        boolean first = true;
        for(Batch b : batches) {
            if(!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append("'").append(b.getBatchID()).append("'");
        }
        
        return sb.toString();
    }
    
    
}
