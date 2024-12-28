// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Clipboard;

/**
 * Stub for avoiding NPEs when the system under test uses Gdx.app.
 */
public class ApplicationStub implements Application {

    @Override
    public ApplicationListener getApplicationListener() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public Audio getAudio() {
        return null;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Files getFiles() {
        return null;
    }

    @Override
    public Net getNet() {
        return null;
    }

    @Override
    public void log(String tag, String message) {
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
    }

    @Override
    public void error(String tag, String message) {
        System.err.println(message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        System.err.println(message);
        exception.printStackTrace();
    }

    @Override
    public void debug(String tag, String message) {
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
    }

    @Override
    public int getLogLevel() {
        return 0;
    }

    @Override
    public void setLogLevel(int logLevel) {
    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return null;
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
    }

    @Override
    public ApplicationType getType() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return 0;
    }

    @Override
    public long getNativeHeap() {
        return 0;
    }

    @Override
    public Preferences getPreferences(String name) {
        return null;
    }

    @Override
    public Clipboard getClipboard() {
        return null;
    }

    @Override
    public void postRunnable(Runnable runnable) {
    }

    @Override
    public void exit() {
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
    }

}
