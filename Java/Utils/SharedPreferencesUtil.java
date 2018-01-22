package com.inspeeding.ys400.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by inspeeding_YF002 on 2017/12/13.
 */

public class SharedPreferencesUtil {
    private SharedPreferences shared;
    private Context context;
    private static SharedPreferencesUtil sharedPreferencesUtil;

    public SharedPreferencesUtil() {
        this.context = BaseApplication.getContext();
        shared = context.getSharedPreferences(SharedPreferenceConfig.SHARED_CONFIG, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        if (sharedPreferencesUtil == null) {
            sharedPreferencesUtil = new SharedPreferencesUtil();
        }
        return sharedPreferencesUtil;
    }

    /**
     * 存储字符串
     *
     * @param key
     * @param values
     */
    public void saveString(String key, String values) {
        SharedPreferences.Editor edit = shared.edit();
        edit.putString(key, values);
        edit.commit();
    }

    /**
     * 获取字符串
     */
    public String getString(String key, String defaultValues) {
        return shared.getString(key, defaultValues);
    }

    /**
     * 存储浮点数
     *
     * @param key
     * @param values
     */
    public void saveFloat(String key, float values) {
        SharedPreferences.Editor edit = shared.edit();
        edit.putFloat(key, values);
        edit.commit();
    }

    /**
     * 获取浮点数
     */
    public float getFloat(String key, float defaultValues) {
        return shared.getFloat(key, defaultValues);
    }



}
