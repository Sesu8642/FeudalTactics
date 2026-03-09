// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.localization;

import com.google.common.io.CharSource;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the localization properties files.
 */
@Slf4j
class LocalizationPropertiesTest {

    public static final String PROPERTIES_BASE_DIR = "../assets/i18n/";
    public static final String FALLBACK_PROPERTIES_NAME = "strings.properties";
    public static final String FALLBACK_PROPERTIES_PATH = PROPERTIES_BASE_DIR + FALLBACK_PROPERTIES_NAME;
    public static final String DEFAULT_PROPERTIES_NAME = "strings_en.properties";

    /**
     * Method source.
     */
    private static Stream<Arguments> translatedPropertiesAreComplete() throws IOException {
        final List<Path> allPropertiesFiles;
        try (Stream<Path> stream = java.nio.file.Files.walk(Paths.get(PROPERTIES_BASE_DIR))) {
            allPropertiesFiles = stream.filter(java.nio.file.Files::isRegularFile).filter(p -> p.toString().endsWith(
                ".properties")).collect(Collectors.toList());
        }
        return allPropertiesFiles.stream()
            // do not compare the fallback to itself
            .filter(path -> !path.getFileName().toString().equals(FALLBACK_PROPERTIES_NAME))
            // do not check the default english file as it is supposed to be empty
            .filter(path -> !path.getFileName().toString().equals(DEFAULT_PROPERTIES_NAME))
            .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource
    void translatedPropertiesAreComplete(Path propertiesFilePath) throws IOException {
        final Path fallbackPropertiesPath = Paths.get(FALLBACK_PROPERTIES_PATH);
        assertTrue(areAllKeysIncluded(propertiesFilePath, fallbackPropertiesPath));
        assertTrue(areAllKeysIncluded(fallbackPropertiesPath, propertiesFilePath));
    }

    /**
     * Returns whether all keys in properties file 1 are also present in properties file 2.
     */
    private boolean areAllKeysIncluded(Path propertiesPath1, Path propertiesPath2) throws IOException {
        final Map<Object, Object> properties1 = loadProperyFile(propertiesPath1);
        final Map<Object, Object> properties2 = loadProperyFile(propertiesPath2);
        boolean result = true;
        for (Object key : properties1.keySet()) {
            if (!properties2.containsKey(key)) {
                result = false;
                log.error("Properties file '{}' has the key '{}' which '{}' is missing.",
                    propertiesPath1.getFileName(), key,
                    propertiesPath2.getFileName());
            }
        }
        return result;
    }

    private Map<Object, Object> loadProperyFile(Path filePath) throws IOException {
        final CharSource source = Files.asCharSource(filePath.toFile(), StandardCharsets.UTF_8);

        final Properties props = new Properties();
        try (java.io.Reader reader = source.openBufferedStream()) {
            props.load(reader);
        }
        return props;
    }

}
