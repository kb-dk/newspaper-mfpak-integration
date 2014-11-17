package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.CommunicationException;
import dk.statsbiblioteket.medieplatform.autonomous.DomsEventStorage;
import dk.statsbiblioteket.medieplatform.autonomous.Event;
import dk.statsbiblioteket.medieplatform.autonomous.EventTrigger;
import dk.statsbiblioteket.medieplatform.autonomous.NewspaperSBOIEventStorage;
import dk.statsbiblioteket.medieplatform.autonomous.PremisManipulatorFactory;
import dk.statsbiblioteket.medieplatform.autonomous.SBOIEventIndex;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MatchMFPakBatchesSBOIEventIndex extends NewspaperSBOIEventStorage {
    public MatchMFPakBatchesSBOIEventIndex(String summaLocation,
                                           PremisManipulatorFactory<Batch> premisManipulatorFactory,
                                           DomsEventStorage<Batch> domsEventStorage, int pageSize) throws
                                                                                              MalformedURLException {
        super(summaLocation, premisManipulatorFactory, domsEventStorage, pageSize);
    }

    @Override
    public Iterator<Batch> getTriggeredItems(Query<Batch> query) throws CommunicationException {
        Iterator<Batch> sboiItems = search(true, query);
        ArrayList<Batch> result = new ArrayList<>();
        while (sboiItems.hasNext()) {
            Batch next = sboiItems.next();
            if (match(next, query)) {
                result.add(next);
            }
        }
        return result.iterator();
    }

    /**
     * Check that the item matches the requirements expressed in the three lists
     *
     * @param item  the item to check
     * @param query query that must be fulfilled
     *
     * @return true if the item match all requirements
     */
    private boolean match(Batch item, Query<Batch> query) {
        Set<String> existingEvents = new HashSet<>();
        Set<String> successEvents = new HashSet<>();
        Set<String> oldEvents = new HashSet<>();
        for (Event event : item.getEventList()) {
            existingEvents.add(event.getEventID());
            if (event.isSuccess()) {
                successEvents.add(event.getEventID());
            }
            if (item.getLastModified() != null) {
                if (!event.getDate().after(item.getLastModified())) {
                    oldEvents.add(event.getEventID());
                }
            }
        }
        final boolean successEventsGood = successEvents.containsAll(query.getPastSuccessfulEvents());


        boolean oldEventsGood = true;
        for (String oldEvent : query.getOldEvents()) {
            oldEventsGood = oldEventsGood && (oldEvents.contains(oldEvent) || !existingEvents.contains(oldEvent));
        }
        boolean futureEventsGood = Collections.disjoint(existingEvents, query.getFutureEvents());

        boolean queryContains = false;
        if (!query.getItems().isEmpty()){
            for (Batch batch : query.getItems()) {
                if (batch.getBatchID().equals(item.getBatchID())){
                    if (batch.getRoundTripNumber() == 0 || batch.getRoundTripNumber().equals(item.getRoundTripNumber())){
                        queryContains = true;
                        break;
                    }

                }
            }

        }

        //TODONT we do not check for types for now
        return successEventsGood && oldEventsGood && futureEventsGood && queryContains;
    }
}
