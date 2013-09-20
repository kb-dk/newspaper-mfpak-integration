package dk.statsbiblioteket.newspaper.mfpakintegration;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MfPakDataSource implements DataSource {
    private Logger log = LoggerFactory.getLogger(getClass());
    private MfPakDAO dao;

    public MfPakDataSource(MfPakConfiguration configuration) {
        dao = new MfPakDAO(configuration);
    }

    /**
     * mfpak manages the physical dispatch of batches and know nothing about Run-Numbers. Therefore this
     * method always returns false in this implementation.
     * @return
     */
    @Override
    public boolean isRunNrInBatchID() {
        return false;
    }

    /**
     * Get all the batches known to this DataSource.
     * @param includeDetails ignored in thus implementation.
     * @param filters ignored in this implementation.
     * @return the batches.
     * @throws NotWorkingProperlyException if there is a problem communicating with the DataSource.
     */
    @Override
    public List<Batch> getBatches(boolean includeDetails, Map<String, String> filters) throws NotWorkingProperlyException {
        try {
            return dao.getAllBatches();
        } catch (Exception e) {
            String message = "mfpak DataSource not working.";
            throw new NotWorkingProperlyException(message, e);
        }
    }

    /**
     * Returns a specific batch given its id (barcode).
     * @param batchID The id of the batch to find.
     * @param includeDetails ignored in this implementation.
     * @return the batch.
     * @throws NotFoundException if the batch doesn't exist.
     * @throws NotWorkingProperlyException if there is a problem communicating with the DataSource.
     */
    @Override
    public Batch getBatch(String batchID, boolean includeDetails) throws NotFoundException, NotWorkingProperlyException {
        int batchIdInt;
        try {
            batchIdInt = Integer.parseInt(batchID);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Batch '" + batchID + "' not found in mfpak (it is not an integer).");
        }
        Batch batch = null;
        try {
            batch = dao.getBatchByBarcode(batchIdInt);
        } catch (Exception e) {
            String message = "mfpak DataSource not working.";
            throw new NotWorkingProperlyException(message, e);
        }
        if (batch == null) {
            throw new NotFoundException("Batch '" + batchID + "' not found.");
        } else {
            return batch;
        }
    }

    /**
     * Return a specific event for a specific batch.
     * @param batchID The id of the event to which the batch is attached.
     * @param eventID The id of the event.
     * @param includeDetails ignored in this implementation.
     * @return the event.
     * @throws NotFoundException if the event doesn't exist.
     * @throws NotWorkingProperlyException if there is a problem communicating with the DataSource.
     */
    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) throws NotFoundException, NotWorkingProperlyException {
        int batchIdInt;
        try {
            batchIdInt = Integer.parseInt(batchID);
        } catch (NumberFormatException e) {
            throw new NotFoundException("Batch '" + batchID + "' not found in mfpak (it is not an integer).");
        }
        Event event = null;
        try {
            event = dao.getEvent(batchIdInt, eventID);
        } catch (Exception e) {
            String message = "mfpak DataSource not working.";
            throw new NotWorkingProperlyException(message, e);
        }
        if (event == null) {
            throw new NotFoundException("Did not find event '" + eventID + "' for batch '" + batchID + "'");
        } else {
            return event;
        }
    }
}
