package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.BatchItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.statsbiblioteket.medieplatform.autonomous.AutonomousComponentUtils;
import dk.statsbiblioteket.medieplatform.autonomous.CallResult;
import dk.statsbiblioteket.medieplatform.autonomous.ConfigConstants;
import dk.statsbiblioteket.medieplatform.autonomous.DomsEventStorage;
import dk.statsbiblioteket.medieplatform.autonomous.DomsEventStorageFactory;
import dk.statsbiblioteket.medieplatform.autonomous.EventStorer;
import dk.statsbiblioteket.medieplatform.autonomous.EventTrigger;
import dk.statsbiblioteket.medieplatform.autonomous.InitialisationException;
import dk.statsbiblioteket.medieplatform.autonomous.PremisManipulatorFactory;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
import dk.statsbiblioteket.medieplatform.autonomous.NewspaperBatchAutonomousComponentUtils;
import dk.statsbiblioteket.medieplatform.autonomous.SBOIEventIndex;
import dk.statsbiblioteket.newspaper.mfpakintegration.configuration.MfPakConfiguration;

import java.util.Properties;

public class MfPakThenSBOIAutonomousComponentUtils extends NewspaperBatchAutonomousComponentUtils {
    private static Logger log = LoggerFactory.getLogger(MfPakThenSBOIAutonomousComponentUtils.class);


    /**
     * Create an autonomous component from a runnable component and start it. Stuff is configured from the included
     * properties
     *
     * @param properties the properties to use
     * @param component  the runnable component to invoke
     *
     * @return the result of the invocation. A map from batch Full IDs to results. If the execution failed, a message
     * will be printed to the log and the result map will be empty
     *
     * autonomous.lockserver.url: string: url to the zookeeper server
     * autonomous.sboi.url: string, url to the summa webservice
     * doms.url: string, url to the fedora doms instance
     * doms.username: string; username when writing events to the doms batch objects
     * doms.password: string: password when writing events to the doms batch objects
     * mfpak.postgres.url: string: URL to MFPAK postgres database.
     * mfpak.postgres.user: string: Username to MFPAK postgres database.
     * mfpak.postgres.password: string: Password to MFPAK postgres database.
     * doms.pidgenerator.url: String: url to the pidgenerator service
     * autonomous.maxThreads: Integer: The number of batches to work on concurrently. Default 1
     * autonomous.maxRuntimeForWorkers: Long: The number of milliseconds to wait before forcibly killing worker
     * threads.
     * Default one hour
     * autonomous.pastSuccessfulEvents: String list, comma separated: The list of event IDs that the batch must have
     * experienced successfully in order to be eligible to be worked on by this component
     * autonomous.futureEvents: String list, comma separated: The list of event IDs that the batch must NOT have
     * experienced in order to be eligible to be worked on by this component
     * autonomous.oldEvents: String list, comma separated: The list of event IDs that the batch must have
     * experienced AFTER last update to the object or not at all
     * autonomous.itemTypes: String list, comma separated: The list of event types (content models) to consider.
     * @see AutonomousComponentUtils#startAutonomousComponent(Properties, RunnableComponent, EventTrigger, EventStorer)
     */
    public static CallResult<Batch> startAutonomousComponent(Properties properties, RunnableComponent<Batch> component) {
        return AutonomousComponentUtils.startAutonomousComponent(properties, component,
                                                                 getEventTrigger(properties),
                                                                 getEventStorer(properties)
        );
    }

    private static synchronized EventTrigger<Batch> getEventTrigger(Properties properties) {
        try {
            MfPakConfiguration mfPakConfiguration = new MfPakConfiguration();
            mfPakConfiguration.setDatabaseUrl(properties.getProperty(ConfigConstants.MFPAK_URL));
            mfPakConfiguration.setDatabaseUser(properties.getProperty(ConfigConstants.MFPAK_USER));
            mfPakConfiguration.setDatabasePassword(properties.getProperty(ConfigConstants.MFPAK_PASSWORD));
            SBOIEventIndex<Batch> sboiEventIndex = new MatchMFPakBatchesSBOIEventIndex(
                    properties.getProperty(ConfigConstants.AUTONOMOUS_SBOI_URL), new PremisManipulatorFactory<>(
                    PremisManipulatorFactory.TYPE, new BatchItemFactory()), getEventStorer(properties),Integer.parseInt(properties.getProperty(ConfigConstants.SBOI_PAGESIZE,"100"))
            );
            return new MfPakEventTriggerThenSBOI(mfPakConfiguration, sboiEventIndex);
        } catch (Exception e) {
            log.error("Unable to initialize event trigger", e);
            throw new InitialisationException("Unable to initialize event trigger", e);
        }
    }

    private static synchronized DomsEventStorage<Batch> getEventStorer(Properties properties) {
        DomsEventStorageFactory<Batch> domsEventStorageFactory = new DomsEventStorageFactory<>();
        domsEventStorageFactory.setFedoraLocation(properties.getProperty(ConfigConstants.DOMS_URL));
        domsEventStorageFactory.setPidGeneratorLocation(properties.getProperty(ConfigConstants.DOMS_PIDGENERATOR_URL));
        domsEventStorageFactory.setUsername(properties.getProperty(ConfigConstants.DOMS_USERNAME));
        domsEventStorageFactory.setPassword(properties.getProperty(ConfigConstants.DOMS_PASSWORD));
        domsEventStorageFactory.setItemFactory(new BatchItemFactory());
        try {
            return domsEventStorageFactory.createDomsEventStorage();
        } catch (Exception e) {
            log.error("Unable to initialize event storage", e);
            throw new InitialisationException("Unable to initialize event storage", e);
        }
    }
}
