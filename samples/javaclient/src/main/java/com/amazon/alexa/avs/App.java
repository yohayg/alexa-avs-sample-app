/**
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Amazon Software License (the "License"). You may not use this file
 * except in compliance with the License. A copy of the License is located at
 * <p>
 * http://aws.amazon.com/asl/
 * <p>
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.amazon.alexa.avs;

import com.amazon.alexa.avs.auth.AuthSetup;
import com.amazon.alexa.avs.config.DeviceConfig;
import com.amazon.alexa.avs.config.DeviceConfigUtils;
import com.amazon.alexa.avs.http.AVSClientFactory;
import com.amazon.alexa.avs.ui.graphical.GraphicalUI;
import com.amazon.alexa.avs.ui.headless.HeadlessUI;
import com.amazon.alexa.avs.ui.BaseUI;
import com.amazon.alexa.avs.wakeword.WakeWordIPCFactory;
import com.diozero.ws281xj.WS281x;

public class App {

    private AVSController controller;
    private AuthSetup authSetup;
    private BaseUI appUI;
    private static WS281x ws281x;

    public static void main(String[] args) throws Exception {
        int gpio_num = 13;
        int brightness = 64;    // 0..255
        //int num_pixels = 12;
        int num_pixels = 12;

        System.out.println("Using GPIO " + gpio_num);
        try {
//            int frequency, int dmaNum, int gpioNum, int brightness, int numPixels, StripType stripType, int channel
//            this.ws281x = new WS281x(800_000, 5, gpio_num, brightness, num_pixels, WS281x.StripType.SK6812W_STRIP, 1);
            ws281x = new WS281x(gpio_num, brightness, num_pixels);

            if (args.length == 1) {
                new App(ws281x,args[0]);
            } else {
                new App(ws281x);
            }
        } catch (Throwable t) {
            System.out.println("Error: " + t);
            t.printStackTrace();
        }
    }

    public App(WS281x ws281x) throws Exception {
        this(ws281x,DeviceConfigUtils.readConfigFile());
    }

    public App(WS281x ws281x, String configName) throws Exception {
        this(ws281x,DeviceConfigUtils.readConfigFile(configName));
    }

    public App(WS281x ws281x, DeviceConfig config) throws Exception {
        authSetup = new AuthSetup(config);
        controller =
                new AVSController(ws281x,new AVSAudioPlayerFactory(), new AlertManagerFactory(),
                        getAVSClientFactory(config), DialogRequestIdAuthority.getInstance(),
                        new WakeWordIPCFactory(), config);
        if (config.getHeadlessModeEnabled()) {
            appUI = new HeadlessUI(controller, authSetup, config);
        } else {
            appUI = new GraphicalUI(controller, authSetup, config);
        }
        config.setApp(this);
    }

    protected AVSClientFactory getAVSClientFactory(DeviceConfig config) {
        return new AVSClientFactory(config);
    }

    protected AVSController getController() {
        return controller;
    }

    public void replaceAVSController(DeviceConfig config) throws Exception {
        controller =
                new AVSController(ws281x,new AVSAudioPlayerFactory(), new AlertManagerFactory(),
                        getAVSClientFactory(config), DialogRequestIdAuthority.getInstance(),
                        new WakeWordIPCFactory(), config);
        appUI.replaceController(controller);
    }
}
