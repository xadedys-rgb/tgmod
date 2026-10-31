/*
 * This is the source code of Telegram for Android v. 7.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2020.
 */

package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

public class ModConfig {

    private static final String PREFS_NAME = "mod_config";
    private static final String KEY_MOD_ENABLED = "mod_enabled";
    private static final String KEY_FAKE_STARS = "fake_stars";
    private static final String KEY_FAKE_GIFT_ANIM = "fake_gift_anim";

    private static final long DEFAULT_FAKE_STARS = 999999L;

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isModEnabled(Context context) {
        return getPrefs(context).getBoolean(KEY_MOD_ENABLED, false);
    }

    public static void setModEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(KEY_MOD_ENABLED, enabled).apply();
    }

    public static long getFakeStars(Context context) {
        return getPrefs(context).getLong(KEY_FAKE_STARS, DEFAULT_FAKE_STARS);
    }

    public static void setFakeStars(Context context, long stars) {
        getPrefs(context).edit().putLong(KEY_FAKE_STARS, stars).apply();
    }

    public static boolean isFakeGiftAnimEnabled(Context context) {
        return getPrefs(context).getBoolean(KEY_FAKE_GIFT_ANIM, false);
    }

    public static void setFakeGiftAnimEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(KEY_FAKE_GIFT_ANIM, enabled).apply();
    }
}