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

    @Override
    public List<Batch> getBatches(boolean includeDetails, Map<String, String> filters) throws NotWorkingProperlyException {
        try {
            return dao.getAllBatches();
        } catch (Exception e) {
            String message = "mfpak DataSource not working.";
            throw new NotWorkingProperlyException(message, e);
        }
    }

    @Override
    public Batch getBatch(String batchID, boolean includeDetails) throws NotFoundException, NotWorkingProperlyException {
        Batch batch = null;
        try {
            batch = dao.getBatchByBarcode(batchID);
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

    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) throws NotFoundException, NotWorkingProperlyException {
        Event event = null;
        try {
            event = dao.getEvent(batchID, eventID);
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
