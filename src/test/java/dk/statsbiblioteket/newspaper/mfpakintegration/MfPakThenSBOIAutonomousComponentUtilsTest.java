package dk.statsbiblioteket.newspaper.mfpakintegration;

import dk.statsbiblioteket.medieplatform.autonomous.Batch;
import dk.statsbiblioteket.medieplatform.autonomous.ConfigConstants;
import dk.statsbiblioteket.medieplatform.autonomous.Item;
import dk.statsbiblioteket.medieplatform.autonomous.ResultCollector;
import dk.statsbiblioteket.medieplatform.autonomous.RunnableComponent;
import dk.statsbiblioteket.newspaper.mfpakintegration.database.ConfigurationProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class MfPakThenSBOIAutonomousComponentUtilsTest {

    @Test(groups = {"integrationTest"}, enabled = false)
    public void testGetEvents() throws Exception {
        Properties properties = ConfigurationProvider.loadProperties();

        properties.setProperty(ConfigConstants.MFPAK_URL,"jdbc:postgresql://PLACEHOLDER/mfpak-stage?ssl=true");
        properties.setProperty(ConfigConstants.MFPAK_USER,"mfpak");
        properties.setProperty(ConfigConstants.MFPAK_PASSWORD,"PLACEHOLDER");

        properties.setProperty(ConfigConstants.AUTONOMOUS_PAST_SUCCESSFUL_EVENTS,"Approved,Data_Archived");
        properties.setProperty(ConfigConstants.AUTONOMOUS_FUTURE_EVENTS, "");
        properties.setProperty(ConfigConstants.AUTONOMOUS_PAST_FAILED_EVENTS, "");

        final Set<Batch> worked = new HashSet<>();

        MfPakThenSBOIAutonomousComponentUtils.startAutonomousComponent(properties,new RunnableComponent<Batch>() {
            @Override
            public String getComponentName() {
                return "TestComponent";
            }

            @Override
            public String getComponentVersion() {
                return "0";
            }

            @Override
            public String getEventID() {
                return "TestEvent";
            }

            @Override
            public void doWorkOnItem(Batch batch, ResultCollector resultCollector) throws Exception {
                worked.add(batch);
            }
        });

        Assert.assertFalse(worked.isEmpty());
    }
}