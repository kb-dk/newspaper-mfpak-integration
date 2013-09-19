package dk.statsbiblioteket.newspaper.mfpakintegration;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.NotFoundException;
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
    public List<Batch> getBatches(boolean includeDetails, Map<String, String> filters) {
        try {
            return dao.getAllBatches();
        } catch (SQLException e) {
            log.error("SQL Error: ", e);
            return null;  //This is wrong behaviour. Should throw a checked exception
        }
    }

    @Override
    public Batch getBatch(String batchID, boolean includeDetails) throws NotFoundException {
        try {
            Batch batch = dao.getBatchByBarcode(batchID);
            if (batch == null) {
                throw new NotFoundException("Batch '" + batchID + "' not found.");
            } else {
                return batch;
            }
        } catch (SQLException e) {
            log.error("SQL Error: ", e);
            return null;  //This is wrong behaviour. Should throw a checked exception
        }
    }

    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) throws NotFoundException {
        try {
            Event event = dao.getEvent(batchID, eventID);
            if (event == null) {
                throw new NotFoundException("Did not find event '" + eventID + "' for batch '" + batchID + "'");
            } else {
                return event;
            }
        } catch (SQLException e) {
            log.error("SQL Error: ", e);
            return null; //This is wrong behaviour. Should throw a checked exception
        }
    }
}
