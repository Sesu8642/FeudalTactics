// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.localization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import de.sesu8642.feudaltactics.FilesStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Locale;
import java.util.MissingResourceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link LocalizationManager}.
 */
class LocalizationManagerTest {

    @BeforeAll
    static void initAll() {
        Gdx.files = new FilesStub();
    }

    @BeforeEach
    void init() {

    }


    @Test
    void autoLanguageAccordingToSystem() {
        // override default locale (otherwise this would be determined from the test host system)
        Locale.setDefault(Locale.KOREAN);
        try (MockedStatic<I18NBundle> bundle = mockStatic(I18NBundle.class)) {

            // the actual locale is determined in the constructor
            // pass AUTO here to have the locale detected according to the system settings
            new LocalizationManager(SupportedLanguages.AUTO);

            final ArgumentCaptor<Locale> captor = ArgumentCaptor.forClass(Locale.class);
            bundle.verify(() -> I18NBundle.createBundle(any(), captor.capture(), any()));
            final Locale passedlocale = captor.getValue();
            assertEquals(Locale.KOREAN, passedlocale);
        }
    }

    @Test
    void missingTranslationFallsBackToEnglish() {
        final I18NBundle bundleWithKeyMissing = Mockito.mock(I18NBundle.class);
        when(bundleWithKeyMissing.format(any())).thenThrow(MissingResourceException.class);
        final I18NBundle bundleWithKeyPresent = Mockito.mock(I18NBundle.class);
        when(bundleWithKeyPresent.format(any())).thenReturn("abc");
        try (MockedStatic<I18NBundle> bundle = mockStatic(I18NBundle.class)) {
            // only the bundle for german is missing the key
            bundle.when(() -> I18NBundle.createBundle(any(), eq(SupportedLanguages.DE_DE.getLocale()), any())).thenReturn(bundleWithKeyMissing);
            bundle.when(() -> I18NBundle.createBundle(any(), eq(SupportedLanguages.getFallback().getLocale()), any())).thenReturn(bundleWithKeyPresent);

            final LocalizationManager systemUnderTest = new LocalizationManager(SupportedLanguages.DE_DE);
            final String actual = systemUnderTest.localizeText("play");

            assertEquals("abc", actual);
        }
    }

    @Test
    void missingPropertyHasFallbackString() {
        final I18NBundle bundleWithMissingKey = Mockito.mock(I18NBundle.class);
        when(bundleWithMissingKey.format(any())).thenThrow(MissingResourceException.class);
        try (MockedStatic<I18NBundle> bundle = mockStatic(I18NBundle.class)) {
            // all bundles are missing the key
            bundle.when(() -> I18NBundle.createBundle(any(), any(), any())).thenReturn(bundleWithMissingKey);

            final LocalizationManager systemUnderTest = new LocalizationManager(SupportedLanguages.DE_DE);
            final String actual = systemUnderTest.localizeText("play");

            assertEquals("[missing: 'play']", actual);
        }
    }


}
