package org.eclipse.smarthome.binding.hue.internal.discovery;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HueBridgeNupnpDiscovery extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(HueBridgeNupnpDiscovery.class);

    private String host = " ";

    private String serialNumber = " ";

    public HueBridgeNupnpDiscovery() {
        super(SUPPORTED_THING_TYPES_UID, 10);
    }

    @Override
    protected void startScan() {

        logger.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        logger.debug("Inside startScan method of HueBridgeNupnpDiscovery...");
        logger.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++");

        discoveryHue();
    }

    private void discoveryHue() {

        // get ThingUID
        ThingUID uid = getThingUID();

        logger.debug("+++++++++++++++++++++");
        logger.debug("The uid is: " + uid);
        logger.debug("+++++++++++++++++++++");

        if (uid != null) {
            Map<String, Object> properties = new HashMap<>(2);
            properties.put(HOST, host);
            properties.put(SERIAL_NUMBER, serialNumber);

            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                    .withLabel("Philips hue").withRepresentationProperty(SERIAL_NUMBER).build();
            thingDiscovered(result);

        } else {
            // do nothing
        }
    }

    private ThingUID getThingUID() {
        try {
            URL url = new URL("https://www.meethue.com/api/nupnp");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = " ";
            while ((strTemp = br.readLine()) != null) {
                String[] parts = strTemp.split(",");
                serialNumber = parts[0].substring(parts[0].indexOf(':') + 2, parts[0].lastIndexOf('"'));
                host = parts[1].substring(parts[1].indexOf(':') + 2, parts[1].lastIndexOf('"')).trim();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (serialNumber != null) {
            return new ThingUID(THING_TYPE_BRIDGE, serialNumber);
        }
        return null;
    }
}
