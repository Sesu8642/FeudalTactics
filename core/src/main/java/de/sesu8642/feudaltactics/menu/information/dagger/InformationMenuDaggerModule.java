// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.ResourceNameReader;

import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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
    static Map<String, Map<String, String>> provideDependencyLicenses(ResourceNameReader resourceNameReader) {
        final List<String> licenseFiles = resourceNameReader.getAssetFiles("dependency_licenses/");
        // outer map key: dependency name, inner map key: file name, outer map value:
        // file contents
        final Map<String, Map<String, String>> result = new HashMap<>();
        for (String assetPath : licenseFiles) {
            final String[] pathParts = assetPath.split("/");
            final String fileName = pathParts[pathParts.length - 1];
            final StringBuilder dependencyNameBuilder = new StringBuilder();
            for (int i = 1; i < pathParts.length - 1; i++) {
                if (dependencyNameBuilder.length() > 0) {
                    dependencyNameBuilder.append(" - ");
                }
                dependencyNameBuilder.append(pathParts[i]);
            }
            final String dependencyName = dependencyNameBuilder.toString();
            final FileHandle fileHandle = Gdx.files.internal(assetPath);
            final String fileContents = fileHandle.readString(StandardCharsets.UTF_8.name());
            result.putIfAbsent(dependencyName, new HashMap<>());
            result.get(dependencyName).put(fileName, fileContents);
        }
        return result;
    }

}
