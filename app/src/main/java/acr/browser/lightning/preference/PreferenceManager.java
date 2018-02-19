package acr.browser.lightning.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferenceManager {

     static final class Name {




























        static final String SEARCH_SUGGESTIONS = "searchSuggestions";




        static final String INITIAL_CHECK_FOR_TOR = "checkForTor";
        static final String INITIAL_CHECK_FOR_I2P = "checkForI2P";


    }

    public enum Suggestion {
        SUGGESTION_GOOGLE,
        SUGGESTION_DUCK,
        SUGGESTION_BAIDU,
        SUGGESTION_NONE
    }

    @NonNull private final SharedPreferences mPrefs;

    private static final String PREFERENCES = "settings";

    @Inject
    PreferenceManager(@NonNull final Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES, 0);
    }

    @NonNull
    public Suggestion getSearchSuggestionChoice() {
        try {
            return Suggestion.valueOf(mPrefs.getString(Name.SEARCH_SUGGESTIONS, Suggestion.SUGGESTION_GOOGLE.name()));
        } catch (IllegalArgumentException ignored) {
            return Suggestion.SUGGESTION_NONE;
        }
    }

    public void setSearchSuggestionChoice(@NonNull Suggestion suggestion) {
        putString(Name.SEARCH_SUGGESTIONS, suggestion.name());
    }

    public boolean getCheckedForTor() {
        return mPrefs.getBoolean(Name.INITIAL_CHECK_FOR_TOR, false);
    }

    public boolean getCheckedForI2P() {
        return mPrefs.getBoolean(Name.INITIAL_CHECK_FOR_I2P, false);
    }

    private void putBoolean(@NonNull String name, boolean value) {
        mPrefs.edit().putBoolean(name, value).apply();
    }

    private void putInt(@NonNull String name, int value) {
        mPrefs.edit().putInt(name, value).apply();
    }

    private void putString(@NonNull String name, @Nullable String value) {
        mPrefs.edit().putString(name, value).apply();
    }

    public void setCheckedForTor(boolean check) {
        putBoolean(Name.INITIAL_CHECK_FOR_TOR, check);
    }

    public void setCheckedForI2P(boolean check) {
        putBoolean(Name.INITIAL_CHECK_FOR_I2P, check);
    }

}
