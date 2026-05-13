// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.localization;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Locale;

/**
 * Contains information about the languages supported in the game.
 */
@EqualsAndHashCode
public class SupportedLanguage {

    public static final SupportedLanguage AUTO = new SupportedLanguage(Locale.getDefault(), true);
    public static final SupportedLanguage FALLBACK = new SupportedLanguage(Locale.forLanguageTag("en"), false);
    private final boolean isAuto;
    @Getter
    private final Locale locale;

    private SupportedLanguage(Locale locale, boolean isAuto) {
        this.locale = locale;
        this.isAuto = isAuto;
    }

    public static SupportedLanguage fromLanguageTag(String languageTag) {
        final Locale localeParam = Locale.forLanguageTag(languageTag);
        return new SupportedLanguage(localeParam, false);
    }

    /**
     * Returns the language tag matching this language.
     */
    public String getLanguageTag() {
        return locale.toLanguageTag();
    }

    /**
     * Returns the display name for the language in the language itself.
     */
    public String getDisplayName() {
        if (isAuto) {
            return "Auto";
        }
        return locale.getDisplayLanguage(locale);
    }
}
