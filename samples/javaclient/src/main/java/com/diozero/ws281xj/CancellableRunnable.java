package com.diozero.ws281xj;

import org.apache.commons.lang3.mutable.MutableBoolean;

public class CancellableRunnable implements   Runnable{
    protected MutableBoolean bool = new MutableBoolean(true);
    @Override
    public void run() {
    }

    public void cancel() {
        bool.setValue(false);
        // usually here you'd have inputStream.close() or connection.disconnect()
    }
}
