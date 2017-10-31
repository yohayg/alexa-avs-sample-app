package com.diozero.ws281xj;

/*
 * #%L
 * Organisation: mattjlewis
 * Project:      Device I/O Zero - WS281x Java Wrapper
 * Filename:     PixelAnimations.java  
 * 
 * This file is part of the diozero project. More information about this project
 * can be found at http://www.diozero.com/
 * %%
 * Copyright (C) 2016 - 2017 mattjlewis
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PixelAnimations {

    private static CancellableRunnable cancellableRunnable;

    public static void delay(int wait) {
        try {
            if (wait > 0) {
                Thread.sleep(wait);
            }
        } catch (InterruptedException e) {
        }
    }

    public static Executor executor = Executors.newSingleThreadExecutor();

    public static void colourWipe(WS281x ws281x, int colour, int wait) {
        cancellableRunnable = new CancellableRunnable() {
            @Override
            public void run() {
                while (bool.booleanValue()) {
                    colorWipe(ws281x, colour, wait);
                }
                ws281x.allOff();
            }
        };
        executor.execute(cancellableRunnable);

    }

    private static void colorWipe(WS281x ws281x, int colour, int wait) {
        for (int i = 0; i < ws281x.getNumPixels(); i++) {
            ws281x.setPixelColour(i, colour);
            ws281x.render();
            delay(wait);
        }
        for (int i = 0; i < ws281x.getNumPixels(); i++) {
            ws281x.setPixelColour(i, 0);
            ws281x.render();
            delay(wait);
        }
    }

    public static void colorWipeOnetime(WS281x ws281x, int color) {
        colorWipe(ws281x, color, 0);
    }

    public static void rainbow(WS281x ws281x, int wait) {

        cancellableRunnable = new CancellableRunnable() {
            @Override
            public void run() {
                while (bool.booleanValue()) {
                    for (int j = 0; j < 256; j++) {
                        for (int i = 0; i < ws281x.getNumPixels(); i++) {
                            ws281x.setPixelColour(i, PixelColour.wheel((i + j) & 255));
                        }
                        ws281x.render();
                        delay(wait);
                    }
                }
            }
        };
        executor.execute(cancellableRunnable);
    }

    /* Slightly different, this makes the rainbow equally distributed throughout */
    public static void rainbowCycle(WS281x ws281x, int wait) {

        // 5 cycles of all colors on wheel
        cancellableRunnable = new CancellableRunnable() {
            @Override
            public void run() {
                while (bool.booleanValue()) {
                    for (int j = 0; j < 256 * 5; j++) { // 5 cycles of all colors on wheel
                        for (int i = 0; i < ws281x.getNumPixels(); i++) {
                            ws281x.setPixelColour(i, PixelColour.wheel(((i * 256 / ws281x.getNumPixels()) + j) & 255));
                        }
                        ws281x.render();
                        delay(wait);
                    }
                }
                ws281x.allOff();
            }

        };
        executor.execute(cancellableRunnable);
    }

    public static void cancelAnimation() {
        cancellableRunnable.cancel();
    }

    /* Theatre-style crawling lights */
    public static void theatreChase(WS281x ws281x, int c, int wait) {
        cancellableRunnable = new CancellableRunnable() {
            @Override
            public void run() {
                while (bool.booleanValue())
                    for (int j = 0; j < 10; j++) {  //do 10 cycles of chasing
                        for (int q = 0; q < 3; q++) {
                            for (int i = 0; i < ws281x.getNumPixels(); i = i + 3) {
                                ws281x.setPixelColour(i + q, c);    //turn every third pixel on
                            }
                            ws281x.render();

                            delay(wait);

                            for (int i = 0; i < ws281x.getNumPixels(); i = i + 3) {
                                ws281x.setPixelColour(i + q, 0);        //turn every third pixel off
                            }
                        }
                    }
            }
        };
        executor.execute(cancellableRunnable);

    }

    /* Theatre-style crawling lights with rainbow effect */
    public static void theatreChaseRainbow(WS281x ws281x, int wait) {

        cancellableRunnable = new CancellableRunnable() {
            @Override
            public void run() {
                while (bool.booleanValue()) {
                    for (int j = 0; j < 256; j++) {     // cycle all 256 colours in the wheel
                        for (int q = 0; q < 3; q++) {
                            for (int i = 0; i < ws281x.getNumPixels(); i = i + 3) {
                                ws281x.setPixelColour(i + q, PixelColour.wheel((i + j) % 255));    //turn every third pixel on
                            }
                            ws281x.render();

                            delay(wait);

                            for (int i = 0; i < ws281x.getNumPixels(); i = i + 3) {
                                ws281x.setPixelColour(i + q, 0);        //turn every third pixel off
                            }
                        }
                    }
                }
            }
        };
        executor.execute(cancellableRunnable);
    }

    public static void wipeWhite(WS281x ws281x) {
        colourWipe(ws281x, PixelColour.createColourRGB(255, 255, 255, 255), 50); // White RGBW
    }

    public static void wipeRed(WS281x ws281x, boolean oneTime) {
        int color = PixelColour.createColourRGB(255, 0, 0, 0);
        if (oneTime) {
            colorWipeOnetime(ws281x, color);
            ws281x.allOff();
        } else {
            colorWipe(ws281x, color, 50);
        }
    }

    public static void wipeGreen(WS281x ws281x) {
        colourWipe(ws281x, PixelColour.createColourRGB(0, 255, 0, 0), 50); // Green
    }

    public static void wipeBlue(WS281x ws281x) {
        colourWipe(ws281x, PixelColour.createColourRGB(0, 0, 255, 0), 50); // Blue
    }
}
