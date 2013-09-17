package dk.statsbiblioteket.newspaper.mfpakintegration;

import java.util.List;
import java.util.Map;

import dk.statsbiblioteket.newspaper.processmonitor.datasources.Batch;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.DataSource;
import dk.statsbiblioteket.newspaper.processmonitor.datasources.Event;

/**
 *
 */
public class MfPak implements DataSource {
    private MfPakConfiguration configuration;
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

    public MfPakConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MfPakConfiguration configuration) {
        this.configuration = configuration;
    }
}
