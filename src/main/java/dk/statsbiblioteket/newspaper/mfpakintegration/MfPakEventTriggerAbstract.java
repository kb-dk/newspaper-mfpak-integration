package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.EventTrigger;
import dk.statsbiblioteket.medieplatform.autonomous.SBOIEventIndex;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.MfPakDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public abstract class MfPakEventTriggerAbstract implements EventTrigger<Batch>,AutoCloseable {

    private static Logger log = LoggerFactory.getLogger(MfPakEventTriggerAbstract.class);
    private final MfPakDAO dao;
    private SBOIEventIndex<Batch> sboiEventIndex;

    public MfPakEventTriggerAbstract(MfPakConfiguration configuration, SBOIEventIndex<Batch> sboiEventIndex) {
        this.sboiEventIndex = sboiEventIndex;
        dao = new MfPakDAO(configuration);
    }

    public MfPakDAO getDao() {
        return dao;
    }

    public SBOIEventIndex<Batch> getSboiEventIndex() {
        return sboiEventIndex;
    }

    public Collection<Batch> asList(Iterator<Batch> sboiResults) {
        ArrayList<Batch> result = new ArrayList<>();
        while (sboiResults.hasNext()) {
            Batch next = sboiResults.next();
            result.add(next);
        }
        return result;

    }


    protected class EventSorter {
        private Collection<String> pastSuccessfulEvents;
        private Collection<String> pastFailedEvents;
        private Collection<String> futureEvents;
        private Collection<String> pastSuccessfulEventsMFPak;
        private Collection<String> pastSuccessfulEventsRest;
        private Collection<String> pastFailedEventsMFPak;
        private Collection<String> pastFailedEventsRest;
        private Collection<String> futureEventsMFPak;
        private Collection<String> futureEventsRest;

        public EventSorter(Collection<String> pastSuccessfulEvents, Collection<String> pastFailedEvents,
                           Collection<String> futureEvents) {
            this.pastSuccessfulEvents = pastSuccessfulEvents;
            this.pastFailedEvents = pastFailedEvents;
            this.futureEvents = futureEvents;
            invoke();
        }

        public Collection<String> getPastSuccessfulEventsMFPak() {
            return pastSuccessfulEventsMFPak;
        }

        public Collection<String> getPastSuccessfulEventsRest() {
            return pastSuccessfulEventsRest;
        }

        public Collection<String> getPastFailedEventsMFPak() {
            return pastFailedEventsMFPak;
        }

        public Collection<String> getPastFailedEventsRest() {
            return pastFailedEventsRest;
        }

        public Collection<String> getFutureEventsMFPak() {
            return futureEventsMFPak;
        }

        public Collection<String> getFutureEventsRest() {
            return futureEventsRest;
        }



        public Collection<String> remove(Collection<String> events, Collection<String> toRemove) {
            Set<String> result = new TreeSet<>(events);
            result.removeAll(toRemove);
            return result;
        }

        public Collection<String> mfPakOnly(Collection<String> events) {

            ArrayList<String> result = new ArrayList<>(events.size());
            for (String event : events) {
                if (EventID.fromFormal(event) != null){
                    result.add(event);
                }
            }
            return result;

        }


        public EventSorter invoke() {
            pastSuccessfulEventsMFPak = mfPakOnly(pastSuccessfulEvents);
            pastSuccessfulEventsRest = remove(pastSuccessfulEvents, pastSuccessfulEventsMFPak);

            pastFailedEventsMFPak = mfPakOnly(pastFailedEvents);
            pastFailedEventsRest = remove(pastFailedEvents, pastFailedEventsMFPak);

            futureEventsMFPak = mfPakOnly(futureEvents);
            futureEventsRest = remove(futureEvents, futureEventsMFPak);
            return this;
        }
    }

    @Override
    public void close() throws Exception {
        dao.close();
    }
}
