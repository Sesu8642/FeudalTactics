// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Dagger module for the information sub-menu and its items.
 */
@Module
public class InformationMenuDaggerModule {

    private InformationMenuDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @DependencyLicenses
    static Map<String, Map<String, String>> provideDependencyLicenses() {
        FileHandle assetsFileHandle = Gdx.files.internal("assets.txt");
        String assetListText = assetsFileHandle.readString(StandardCharsets.UTF_8.name());
        String[] assets = assetListText.split("\n");
        // outer map key: dependency name, inner map key: file name, outer map value:
        // file contents
        Map<String, Map<String, String>> result = new HashMap<>();
        for (int i = 0; i < assets.length; i++) {
            String assetPath = assets[i];
            if (!assetPath.startsWith("dependency_licenses/")) {
                continue;
            }
            String[] pathParts = assetPath.split("/");
            String fileName = pathParts[pathParts.length - 1];
            StringBuilder dependencyNameBuilder = new StringBuilder();
            for (int j = 1; j < pathParts.length - 1; j++) {
                if (dependencyNameBuilder.length() > 0) {
                    dependencyNameBuilder.append(" - ");
                }
                dependencyNameBuilder.append(pathParts[j]);
            }
            String dependencyName = dependencyNameBuilder.toString();
            FileHandle fileHandle = Gdx.files.internal(assetPath);
            String fileContents = fileHandle.readString(StandardCharsets.UTF_8.name());
            result.putIfAbsent(dependencyName, new HashMap<>());
            result.get(dependencyName).put(fileName, fileContents);
        }
        return result;
    }

}
