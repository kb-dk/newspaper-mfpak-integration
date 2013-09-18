package dk.statsbiblioteket.newspaper.mfpakintegration;

import java.util.List;
import java.util.Map;

import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;

/**
 *
 */
public class MfPakDataSource implements DataSource {
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
        return null;
    }

    @Override
    public Batch getBatch(String batchID, boolean includeDetails) {
        return null;
    }

    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) {
        return null;
    }
}
