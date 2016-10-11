package org.eclipse.smarthome.binding.hue.internal.discovery;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HueBridgeNupnpDiscovery extends AbstractDiscoveryService {

    // private Logger logger = LoggerFactory.getLogger(HueBridgeNupnpDiscovery.class);

    private String host = " ";

    private String serialNumber = " ";

    public HueBridgeNupnpDiscovery() {
        super(SUPPORTED_THING_TYPES_UID, 10);
    }

    @Override
    protected void startScan() {

        discoveryHue();
    }

    private void discoveryHue() {

        // get ThingUID
        ThingUID uid = getThingUID();

        if (uid != null) {
            Map<String, Object> properties = new HashMap<>(2);
            properties.put(HOST, host);
            properties.put(SERIAL_NUMBER, serialNumber);

            // TODO: Label of the Philips hue is hard coded, it is possible to read it from web page -
            // bridgeIp/description.xml
            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                    .withLabel("Philips hue").withRepresentationProperty(SERIAL_NUMBER).build();
            thingDiscovered(result);

        } else {
            // do nothing
        }
    }

    private ThingUID getThingUID() {

        Gson gson = new Gson();
        try {
            String json = readUrl("https://www.meethue.com/api/nupnp");

            List<JsonParameters> list = gson.fromJson(json, new TypeToken<List<JsonParameters>>() {
            }.getType());

            // TODO: in case of more than one bridge, code should be modified
            // in case of one bridge
            JsonParameters jsonParameters = list.get(0);

            serialNumber = jsonParameters.getId();
            host = jsonParameters.getInternalipaddress();

            if (serialNumber != null) {
                return new ThingUID(THING_TYPE_BRIDGE, serialNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }
}
