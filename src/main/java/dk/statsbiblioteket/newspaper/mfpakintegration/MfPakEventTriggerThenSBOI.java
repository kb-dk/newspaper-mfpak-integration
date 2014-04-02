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

public class MfPakEventTriggerThenSBOI extends MfPakEventTriggerAbstract implements EventTrigger {

    private static Logger log = LoggerFactory.getLogger(MfPakEventTriggerThenSBOI.class);

    public MfPakEventTriggerThenSBOI(MfPakConfiguration configuration, SBOIEventIndex sboiEventIndex) {
        super(configuration, sboiEventIndex);
    }


    @Override
    public Iterator<Batch> getTriggeredBatches(Collection<String> pastSuccessfulEvents,
                                               Collection<String> pastFailedEvents, Collection<String> futureEvents,
                                               Batch... batches) throws CommunicationException {
        EventSorter events = new EventSorter(pastSuccessfulEvents, pastFailedEvents, futureEvents);


        Iterator<Batch> mfPakResult = getDao().getTriggeredBatches(
                events.getPastSuccessfulEventsMFPak(),
                events.getPastFailedEventsMFPak(),
                events.getFutureEventsMFPak(),
                batches);


        if (mfPakResult.hasNext()) {
            return getSboiEventIndex().getTriggeredBatches(
                    events.getPastSuccessfulEventsRest(),
                    events.getPastFailedEventsRest(),
                    events.getFutureEventsRest(),
                    asArray(mfPakResult));
        } else {
            return mfPakResult;
        }
    }


}
