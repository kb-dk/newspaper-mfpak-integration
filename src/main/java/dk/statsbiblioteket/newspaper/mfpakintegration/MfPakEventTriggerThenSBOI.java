package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.CommunicationException;
import dk.statsbiblioteket.medieplatform.autonomous.EventTrigger;
import dk.statsbiblioteket.medieplatform.autonomous.Item;
import dk.statsbiblioteket.medieplatform.autonomous.SBOIEventIndex;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MfPakEventTriggerThenSBOI extends MfPakEventTriggerAbstract implements EventTrigger<Batch> {

    private static Logger log = LoggerFactory.getLogger(MfPakEventTriggerThenSBOI.class);

    public MfPakEventTriggerThenSBOI(MfPakConfiguration configuration, SBOIEventIndex<Batch> sboiEventIndex) {
        super(configuration, sboiEventIndex);
    }


    @Override
    public Iterator<Batch> getTriggeredItems(Query<Batch> query) throws CommunicationException {
        EventSorter events = new EventSorter(query.getPastSuccessfulEvents(), query.getFutureEvents());


        Iterator<Batch> mfPakResult;
        try {
            mfPakResult = getDao().getTriggeredBatches(events.getPastSuccessfulEventsMFPak(),
                                                              events.getFutureEventsMFPak(),
                                                              query.getItems());
        } catch (SQLException e) {
            throw new CommunicationException(e);
        }


        Query<Batch> query2 = new Query<>();
        query2.getPastSuccessfulEvents().addAll(events.getPastSuccessfulEventsRest());
        query2.getFutureEvents().addAll(events.getFutureEventsRest());
        query2.getItems().addAll(asList(mfPakResult));
        if (mfPakResult.hasNext()) {
            return getSboiEventIndex().getTriggeredItems(query2);
        } else { //TODO: Notice that we do NOT merge the mfpak batches with the SBOI batches here. This is left as an exercise to the user....
            return mfPakResult;
        }
    }





}
