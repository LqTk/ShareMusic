package tk.com.sharemusic.utils;

import java.util.ArrayList;

public interface IConfig {
    void loadConfig();
    boolean isLoadConfig();

    void open();
    void close();

    boolean isClosed();

    void setString(String key, String value);
    void setInt(String key, int value);
    void setBoolean(String key, boolean value);
    void setByte(String key, byte[] value);
    void setShort(String key, short value);
    void setLong(String key, long value);
    void setFloat(String key, float value);
    void setDouble(String key, double value);
    void setObject(String key, Object o);

    String getString(String key, String defaultValue);
    int getInt(String key, int defaultValue);
    long getLong(String key, long defaultValue);
    boolean getBoolean(String key, boolean defaultValue);
    byte[] getByte(String key, byte[] defaultValue);
    short getShort(String key, short defaultValue);
    float getFloat(String key, float defaultValue);
    double getDouble(String key, double defaultValue);
    <T extends Object> T getObject(String key, Class<T> tClass);
    <T> ArrayList<T> getArrayList(String key, Class<T> tClass);

    void remove(String key);
    void remove(String... key);

    void clear();
}
