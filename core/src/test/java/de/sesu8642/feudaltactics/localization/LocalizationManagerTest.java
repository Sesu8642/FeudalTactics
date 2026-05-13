// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.localization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.FilesStub;
import de.sesu8642.feudaltactics.ResourceNameReader;
import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
@ExtendWith(MockitoExtension.class)
class LocalizationManagerTest {

    @Mock
    private MainPreferencesDao mainPreferencesDaoMock;
    @Mock
    private ResourceNameReader resourceNameReaderMock;

    @BeforeAll
    static void initAll() {
        Gdx.files = new FilesStub();
    }

    @Test
    void autoLanguageAccordingToSystem() {
        // returning the AUTO setting here to have the locale detected according to the system settings
        when(mainPreferencesDaoMock.getMainPreferences()).thenReturn(new MainGamePreferences(false, false,
            SupportedLanguage.AUTO));

        try (MockedStatic<I18NBundle> bundle = mockStatic(I18NBundle.class)) {

            // the actual locale is determined in the constructor
            new LocalizationManager(mainPreferencesDaoMock, resourceNameReaderMock);

            final ArgumentCaptor<Locale> captor = ArgumentCaptor.forClass(Locale.class);
            bundle.verify(() -> I18NBundle.createBundle(any(), captor.capture(), any()));
            final Locale passedlocale = captor.getValue();
            assertEquals(Locale.getDefault(), passedlocale);
        }
    }

    @Test
    void missingTranslationFallsBackToEnglish() {
        when(mainPreferencesDaoMock.getMainPreferences()).thenReturn(new MainGamePreferences(false, false,
            SupportedLanguage.fromLanguageTag("de")));
        final I18NBundle bundleWithKeyMissing = Mockito.mock(I18NBundle.class);
        when(bundleWithKeyMissing.format(any())).thenThrow(MissingResourceException.class);
        final I18NBundle bundleWithKeyPresent = Mockito.mock(I18NBundle.class);
        when(bundleWithKeyPresent.format(any())).thenReturn("abc");
        try (MockedStatic<I18NBundle> bundle = mockStatic(I18NBundle.class)) {
            // only the bundle for german is missing the key
            bundle.when(() -> I18NBundle.createBundle(any(), eq(Locale.forLanguageTag("de")), any()))
                .thenReturn(bundleWithKeyMissing);
            bundle.when(() -> I18NBundle.createBundle(any(), eq(SupportedLanguage.FALLBACK.getLocale()), any()
            )).thenReturn(bundleWithKeyPresent);

            final LocalizationManager systemUnderTest = new LocalizationManager(mainPreferencesDaoMock,
                resourceNameReaderMock);
            final String actual = systemUnderTest.localizeText(TranslationKeys.MENU_BUTTON_PLAY);

            assertEquals("abc", actual);
        }
    }

    @Test
    void missingPropertyHasFallbackString() {
        when(mainPreferencesDaoMock.getMainPreferences()).thenReturn(new MainGamePreferences(false, false,
            SupportedLanguage.fromLanguageTag("de")));
        final I18NBundle bundleWithMissingKey = Mockito.mock(I18NBundle.class);
        when(bundleWithMissingKey.format(any())).thenThrow(MissingResourceException.class);
        try (MockedStatic<I18NBundle> bundle = mockStatic(I18NBundle.class)) {
            // all bundles are missing the key
            bundle.when(() -> I18NBundle.createBundle(any(), any(), any())).thenReturn(bundleWithMissingKey);

            final LocalizationManager systemUnderTest = new LocalizationManager(mainPreferencesDaoMock,
                resourceNameReaderMock);
            final String actual = systemUnderTest.localizeText(TranslationKeys.MENU_BUTTON_PLAY);

            assertEquals("[missing: 'menu-button-play']", actual);
        }
    }


}
