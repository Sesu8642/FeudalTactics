// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

/**
 * Stub for avoiding NPEs when the system under test uses Gdx.files.
 */
public class FilesStub implements Files {

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        return null;
    }

    @Override
    public FileHandle classpath(String path) {
        return null;
    }

    @Override
    public FileHandle internal(String path) {
        return new FileHandle(path);
    }

    @Override
    public FileHandle external(String path) {
        return null;
    }

    @Override
    public FileHandle absolute(String path) {
        return null;
    }

    @Override
    public FileHandle local(String path) {
        return null;
    }

    @Override
    public String getExternalStoragePath() {
        return "";
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return false;
    }

    @Override
    public String getLocalStoragePath() {
        return "";
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return false;
    }
}
