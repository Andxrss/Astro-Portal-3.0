package com.anderson.chewy.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InactivityManager implements AWTEventListener {
    private final Application app;
    private final Timer timer;

    public InactivityManager(Application app) {
        final long APPLICATION_EVENT_MASK = AWTEvent.KEY_EVENT_MASK
                | AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK
                | AWTEvent.MOUSE_WHEEL_EVENT_MASK;
        final int INTERVAL = 60 * 1000;

        this.app = app;
        timer = new Timer(INTERVAL, e -> app.reset());
        timer.setRepeats(false);

        Toolkit.getDefaultToolkit().addAWTEventListener(this, APPLICATION_EVENT_MASK);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    /* Reset the timer every time activity is detected */
    public void eventDispatched(AWTEvent event) {
        if (timer.isRunning()
                && app.getCurrentScene() != Scene.MAIN_SCENE
                && app.getCurrentScene() != Scene.END_SCENE) {
            timer.restart();
        }
    }
}
