package tk.com.sharemusic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PreferenceConfig implements IConfig {

    private static PreferenceConfig mPreferenceConfig;

    private Context context;

    private SharedPreferences.Editor editor = null;

    private SharedPreferences sharedPreferences;

    private String fileName = "sharedata";

    private Boolean isLoad = false;

    private PreferenceConfig(Context context){
        this.context = context;
    }

    public static PreferenceConfig getPreferenceConfig(Context context){
        if (mPreferenceConfig == null){
            mPreferenceConfig = new PreferenceConfig(context);
        }
        return mPreferenceConfig;
    }

    public static IConfig getPreConfig(Context context){
        if (mPreferenceConfig == null){
            mPreferenceConfig = new PreferenceConfig(context);
        }
        return mPreferenceConfig;
    }

    @Override
    public void loadConfig() {
        try {
            sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            isLoad = true;
        }catch (Exception e){
            isLoad = false;
        }
    }

    @Override
    public boolean isLoadConfig() {
        return isLoad;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void setString(String key, String value) {
        editor.putString(key,value);
        editor.apply();
    }

    @Override
    public void setInt(String key, int value) {
        editor.putInt(key,value);
        editor.apply();
    }

    @Override
    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key,value);
        editor.apply();
    }

    @Override
    public void setByte(String key, byte[] value) {
        setString(key,String.valueOf(value));
    }

    @Override
    public void setShort(String key, short value) {
        setString(key,String.valueOf(value));
    }

    @Override
    public void setLong(String key, long value) {
        editor.putLong(key,value);
        editor.apply();
    }

    @Override
    public void setFloat(String key, float value) {
        editor.putFloat(key,value);
        editor.apply();
    }

    @Override
    public void setDouble(String key, double value) {
        setString(key,String.valueOf(value));
    }

    @Override
    public void setObject(String key, Object o) {
        String json = new Gson().toJson(o);
        setString(key, json);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key,defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key,defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key,defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key,defaultValue);
    }

    @Override
    public byte[] getByte(String key, byte[] defaultValue) {
        try {
            return getString(key,"").getBytes();
        }catch (Exception e){

        }
        return defaultValue;
    }

    @Override
    public short getShort(String key, short defaultValue) {
        try {
            return Short.valueOf(getString(key,""));
        }catch (Exception e){

        }
        return defaultValue;
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key,defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.valueOf(getString(key,""));
        }catch (Exception e){

        }
        return defaultValue;
    }

    @Override
    public <T> T getObject(String key, Class<T> tClass) {
        String json = getString(key,"");
        return new Gson().fromJson(json,tClass);
    }

    @Override
    public <T> ArrayList<T> getArrayList(String key, Class<T> tClass) {
        String json = getString(key,"");
        if (TextUtils.isEmpty(json)){
            return new ArrayList<>();
        }
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(json);
        JsonArray jsonArray = null;
        if (jsonElement.isJsonArray()){
            jsonArray = jsonElement.getAsJsonArray();
        }
        List<T> list = new ArrayList<>();
        Iterator it = jsonArray.iterator();
        while (it.hasNext()){
            JsonElement e = (JsonElement) it.next();
            list.add((T)new Gson().fromJson(e,tClass));
        }
        return (ArrayList<T>) list;
    }

    @Override
    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    @Override
    public void remove(String... key) {
        for (String k:key)
            remove(k);
    }

    @Override
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
