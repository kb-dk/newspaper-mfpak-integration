package dk.statsbiblioteket.newspaper.mfpakintegration;

import java.util.ArrayList;
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
        Batch batch1 = new Batch();
        Batch batch2 = new Batch();
        batch1.setBatchID("4001");
        batch2.setBatchID("4002");
        batch2.setEventList(new ArrayList<Event>());
        List<Event> events = new ArrayList<Event>();
        Event event1 = new Event();
        event1.setEventID("Shipped");
        event1.setSuccess(true);
        Event event2 = new Event();
        event2.setEventID("Received");
        event2.setSuccess(true);
        events.add(event1);
        events.add(event2);
        batch2.setEventList(events);
        List<Batch> batches = new ArrayList<Batch>();
        batches.add(batch1);
        batches.add(batch2);
        return batches;
    }

    @Override
    public Batch getBatch(String batchID, boolean includeDetails) {
        Batch returnValue = new Batch();
        returnValue.setBatchID(batchID);
        returnValue.setEventList(new ArrayList<Event>() {});
        return returnValue;
    }

    @Override
    public Event getBatchEvent(String batchID, String eventID, boolean includeDetails) {
        Event returnValue = new Event();
        returnValue.setEventID(eventID);
        if (includeDetails) returnValue.setDetails("Here are some details.");
        returnValue.setSuccess(true);
        return returnValue;
    }
}
