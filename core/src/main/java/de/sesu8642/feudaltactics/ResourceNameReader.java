// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import lombok.NoArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For getting resource file names from assets.txt.
 */
@Singleton
@NoArgsConstructor(onConstructor_ = @Inject)
public class ResourceNameReader {

    /**
     * Returns a list of all asset files whose path begins with the given string.
     */
    public List<String> getAssetFiles(String startOfPath) {
        final FileHandle assetsFileHandle = Gdx.files.internal("assets.txt");
        final String assetListText = assetsFileHandle.readString(StandardCharsets.UTF_8.name());
        final String[] assets = assetListText.split("\n");
        return Arrays.stream(assets).filter(assetPath -> assetPath.startsWith(startOfPath)).collect(Collectors.toList());
    }

}
