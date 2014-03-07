package dk.statsbiblioteket.newspaper.mfpakintegration.batchcontext;

import java.sql.SQLException;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;

/**
 * Util class to work with BatchContext objects 
 */
public class BatchContextUtils {

    /**
     * Method to obtain a populated BatchContext object.
     * @param mfPakDAO The DAO to extract information with.
     * @param batch The batch to extract the information for. 
     * @throws SQLException in case of SQL errors.
     */
    public static BatchContext buildBatchContext(MfPakDAO mfPakDAO, Batch batch) throws SQLException {
        BatchContext context = new BatchContext(batch);
        
        context.setEntities(mfPakDAO.getBatchNewspaperEntities(batch.getBatchID()));
        context.setAvisId(mfPakDAO.getNewspaperID(batch.getBatchID()));
        context.setBatchOptions(mfPakDAO.getBatchOptions(batch.getBatchID()));
        context.setDateRanges(mfPakDAO.getBatchDateRanges(batch.getBatchID()));
        context.setShipmentDate(mfPakDAO.getBatchShipmentDate(batch.getBatchID()));
        
        verifyBatchContext(context);
        
        return context;
    }
    
    
    private static void verifyBatchContext(BatchContext context) {
        if(context.getAvisId() == null) {
            throw new InvalidBatchContextException("No 'avisID' could be found for batch '" 
                    + context.getBatch().getBatchID() + "'");
        }
        if(context.getBatchOptions() == null) {
            throw new InvalidBatchContextException("No order options could be found for batch '" 
                    + context.getBatch().getBatchID() + "'. "
                    + "This might mean that no order have been placed for that batch.");
        }
        if(context.getShipmentDate() == null) {
            throw new InvalidBatchContextException("No shipment date for batch '" 
                    + context.getBatch().getBatchID() + "' could be found. "
                    + "This might mean that the batch have not been shipped yet.");
        }
        if(context.getDateRanges() == null) {
            throw new InvalidBatchContextException("No date ranges for batch '" + context.getBatch().getBatchID() 
                    + "' could be found.");
        }
        if(context.getEntities() == null) {
            throw new InvalidBatchContextException("No title and publication information could be found "
                    + "for batch '" + context.getBatch().getBatchID() + "'");
        }
    }
}
