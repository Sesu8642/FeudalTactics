// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.localization;

import java.util.Locale;

/**
 * Contains information about the languages supported in the game.
 */
public enum SupportedLanguages {

    /**
     * Follows system locale, if possible; defaults to English.
     */
    AUTO(null),
    EN_US("en-US"),
    DE_DE("de-DE");

    private Locale locale;

    SupportedLanguages(String languageTag) {
        if (languageTag != null) {
            locale = Locale.forLanguageTag(languageTag);
        }
    }

    /**
     * Returns the language matching the given language tag or AUTO if there is no match.
     */
    public static SupportedLanguages fromLanguageTag(String languageTag) {
        for (SupportedLanguages language : values()) {
            if (language.locale != null && language.locale.toLanguageTag().equalsIgnoreCase(languageTag)) {
                return language;
            }
        }
        return AUTO;
    }

    /**
     * Returns the fallback display language that is used if the user's language is not supported.
     */
    public static SupportedLanguages getFallback() {
        return EN_US;
    }

    /**
     * Returns the locale matching this language.
     */
    public Locale getLocale() {
        return locale != null ? locale : Locale.getDefault();
    }

    /**
     * Returns the language tag matching this language or "auto".
     */
    public String getLanguageTag() {
        return locale != null ? locale.toLanguageTag() : "auto";
    }

    /**
     * Returns the display name for the language in the language itself.
     */
    public String getDisplayName() {
        if (this == AUTO) {
            return "Auto";
        }
        return getLocale().getDisplayLanguage(getLocale());
    }
}
