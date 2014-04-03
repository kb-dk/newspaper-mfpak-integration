package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.CommunicationException;
import dk.statsbiblioteket.medieplatform.autonomous.EventTrigger;
import dk.statsbiblioteket.medieplatform.autonomous.SBOIEventIndex;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;

public class MfPakEventTriggerAfterSBOI extends MfPakEventTriggerAbstract implements EventTrigger {

    private static Logger log = LoggerFactory.getLogger(MfPakEventTriggerAfterSBOI.class);

    public MfPakEventTriggerAfterSBOI(MfPakConfiguration configuration, SBOIEventIndex sboiEventIndex) {
        super(configuration, sboiEventIndex);
    }


    @Override
    public Iterator<Batch> getTriggeredBatches(Collection<String> strings, Collection<String> strings2,
                                               Collection<String> strings3) throws CommunicationException {
        return getTriggeredBatches(strings,strings2,strings3,null);
    }

    @Override
    public Iterator<Batch> getTriggeredBatches(Collection<String> pastSuccessfulEvents,
                                               Collection<String> pastFailedEvents, Collection<String> futureEvents,
                                               Collection<Batch> batches) throws CommunicationException {
        EventSorter events = new EventSorter(pastSuccessfulEvents, pastFailedEvents, futureEvents);

        Iterator<Batch> sboiResults = getSboiEventIndex().getTriggeredBatches(
                events.getPastSuccessfulEventsRest(),
                events.getPastFailedEventsRest(),
                events.getFutureEventsRest(),
                batches);

        if (sboiResults.hasNext()) {
            return getDao().getTriggeredBatches(
                    events.getPastSuccessfulEventsMFPak(),
                    events.getPastFailedEventsMFPak(),
                    events.getFutureEventsMFPak(),
                    asList(sboiResults));
        } else {
            return sboiResults;
        }
    }



}
