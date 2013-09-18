package dk.statsbiblioteket.newspaper.mfpakintegration;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MfPakDataSource implements DataSource {
    private MfPakDAO dao;

    /** The log.*/
      private Logger log = LoggerFactory.getLogger(getClass());

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
            return null;
        }
    }

    @Override
    public Batch getBatch(String batchID, boolean includeDetails) {
        try {
            return dao.getBatchByBarcode(batchID);
        } catch (SQLException e) {
            log.error("SQL Error: ", e);
            return null;
        }
    }

    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) {
        try {
            return dao.getEvent(batchID, eventID);
        } catch (SQLException e) {
            log.error("SQL Error: ", e);
            return null;
        }
    }
}
