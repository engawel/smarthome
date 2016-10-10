/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.hue.internal.discovery;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * The {@link HueBridgeNupnpDiscovery} is responsible for discovering new
 * hue bridges. It uses the 'NUPnP service provided by Philips'.
 *
 * @author Awelkiyar Wehabrebi - Initial contribution
 *
 */
public class HueBridgeNupnpDiscovery extends AbstractDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(HueBridgeNupnpDiscovery.class);

    private static final String url = "https://www.meethue.com/api/nupnp";

    private static final int timeout = 5000;

    private static final int BRIDGE_SCAN_INTERVAL = 60;

    private ScheduledFuture<?> bridgeScanningJob;

    public HueBridgeNupnpDiscovery() {
        super(SUPPORTED_THING_TYPES_UID, 10);
    }

    @Override
    protected void startBackgroundDiscovery() {
        if (bridgeScanningJob == null || bridgeScanningJob.isCancelled()) {
            this.bridgeScanningJob = scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    discoverHueBridges();
                }
            }, 0, BRIDGE_SCAN_INTERVAL, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void stopBackgroundDiscovery() {
        if (bridgeScanningJob != null || !bridgeScanningJob.isCancelled()) {
            bridgeScanningJob.cancel(true);
            bridgeScanningJob = null;
        }
    }

    @Override
    protected void startScan() {
        discoverHueBridges();
    }

    private void discoverHueBridges() {
        // get bridge list
        List<BridgeJsonParameters> bridgeList = getbridgeList();
        if (bridgeList != null) {
            for (BridgeJsonParameters bridge : bridgeList) {
                String serialNumber = bridge.getId();
                String host = bridge.getInternalipaddress();
                if (serialNumber != null && host != null) {
                    // check if bridge is reachable
                    if (isHueBridge(host)) {
                        ThingUID uid = new ThingUID(THING_TYPE_BRIDGE, bridge.getId());
                        if (uid != null) {
                            Map<String, Object> properties = new HashMap<>(2);
                            properties.put(HOST, host);
                            properties.put(SERIAL_NUMBER, serialNumber);
                            DiscoveryResult result = DiscoveryResultBuilder.create(uid).withProperties(properties)
                                    .withLabel("Philips hue (" + host + ")").withRepresentationProperty(SERIAL_NUMBER)
                                    .build();
                            thingDiscovered(result);
                        }
                    }
                }
            }
        }
    }

    // method to check if bridge is available and is Hue Bridge
    private boolean isHueBridge(String host) {
        try {
            String url = "http://" + host + "/description.xml";
            String description = HttpUtil.executeUrl("GET", url, timeout);
            return description.contains("<modelName>Philips hue");
        } catch (IOException e) {
            logger.error("Error accessing the bridge description.xml file - bridge might be not reachable");
            e.printStackTrace();
        }
        return false;
    }

    // method to return list of Bridges
    private List<BridgeJsonParameters> getbridgeList() {
        Gson gson = new Gson();
        try {
            String json = HttpUtil.executeUrl("GET", url, timeout);
            return gson.fromJson(json, new TypeToken<List<BridgeJsonParameters>>() {
            }.getType());
        } catch (IOException e) {
            logger.error("Error accessing this url - https://www.meethue.com/api/nupnp");
            e.printStackTrace();
        }
        return null;
    }

}
