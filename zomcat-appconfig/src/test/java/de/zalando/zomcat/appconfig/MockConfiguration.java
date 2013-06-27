package de.zalando.zomcat.appconfig;

import java.lang.reflect.Type;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import com.google.common.collect.Maps;

import de.zalando.appconfig.ConfigCtx;
import de.zalando.appconfig.Configuration;
import de.zalando.appconfig.ConfigurationResource;
import de.zalando.appconfig.cache.ConfigurationCache;

public class MockConfiguration implements Configuration {

    private final Map<String, String> values = Maps.newHashMap();

    private String getKey(final String key) {
        return getKey(key, null);
    }

    private String getKey(final String key, final Integer appDomainId) {
        String intValue = appDomainId == null ? "null" : appDomainId.toString();
        return key + "_" + intValue;
    }

    public void setValue(final String key, final String value) {
        values.put(getKey(key, null), value);
    }

    public void setValue(final String key, final Integer appDomainId, final String value) {
        values.put(getKey(key, appDomainId), value);
    }

    @Override
    public String getString(final String s, final Integer integer) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getString(final String s, final Integer integer, final String s2) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getStringConfig(final String s) {
        return values.get(getKey(s));
    }

    @Override
    public String getStringConfig(final String s, final ConfigCtx configCtx) {
        String value = null;
        if (configCtx.getAppDomainId() == null) {
            value = values.get(getKey(s));
        } else {
            value = values.get(getKey(s, configCtx.getAppDomainId()));
            if (value == null) {
                value = values.get(getKey(s));
            }
        }

        return value;
    }

    @Override
    public String getStringConfig(final String s, final ConfigCtx configCtx, final String s2) {
        String value = null;
        if (configCtx == null || configCtx.getAppDomainId() == null) {
            value = values.get(getKey(s));
        } else {
            value = values.get(getKey(s, configCtx.getAppDomainId()));
            if (value == null) {
                value = values.get(getKey(s));
            }
        }

        return value == null ? s2 : value;
    }

    @Override
    public boolean getBoolean(final String s, final Integer integer) {
        return false; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBoolean(final String s, final Integer integer, final boolean b) {
        return false; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean getBoolean(final String s, final Integer integer, final Boolean aBoolean) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBooleanConfig(final String s) {
        return false; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBooleanConfig(final String s, final ConfigCtx configCtx) {
        return false; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getBooleanConfig(final String s, final ConfigCtx configCtx, final boolean b) {
        return false; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean getBooleanConfig(final String s, final ConfigCtx configCtx, final Boolean aBoolean) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLong(final String s, final Integer integer) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLong(final String s, final Integer integer, final long l) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getLong(final String s, final Integer integer, final Long aLong) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLongConfig(final String s) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLongConfig(final String s, final ConfigCtx configCtx) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLongConfig(final String s, final ConfigCtx configCtx, final long l) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getLongConfig(final String s, final ConfigCtx configCtx, final Long aLong) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getInteger(final String s, final Integer integer) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getInteger(final String s, final Integer integer, final int i) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getInteger(final String s, final Integer integer, final Integer integer2) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getIntegerConfig(final String s) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getIntegerConfig(final String s, final ConfigCtx configCtx) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getIntegerConfig(final String s, final ConfigCtx configCtx, final int i) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getIntegerConfig(final String s, final ConfigCtx configCtx, final Integer integer) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByte(final String s, final Integer integer) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByte(final String s, final Integer integer, final byte b) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Byte getByte(final String s, final Integer integer, final Byte aByte) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByteConfig(final String s) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByteConfig(final String s, final ConfigCtx configCtx) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByteConfig(final String s, final ConfigCtx configCtx, final byte b) {
        return 0; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Byte getByteConfig(final String s, final ConfigCtx configCtx, final Byte aByte) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LocalTime getLocalTime(final String s, final Integer integer) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LocalTime getLocalTime(final String s, final Integer integer, final LocalTime localTime) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LocalTime getLocalTimeConfig(final String s) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LocalTime getLocalTimeConfig(final String s, final ConfigCtx configCtx) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LocalTime getLocalTimeConfig(final String s, final ConfigCtx configCtx, final LocalTime localTime) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DateTime getDateTimeConfig(final String s) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DateTime getDateTimeConfig(final String s, final ConfigCtx configCtx) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DateTime getDateTimeConfig(final String s, final ConfigCtx configCtx, final DateTime dateTime) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getStringArray(final String s, final Integer integer) {
        return new String[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getStringArrayConfig(final String s) {
        return new String[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getStringArrayConfig(final String s, final ConfigCtx configCtx) {
        return new String[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getStringArray(final String s, final Integer integer, final boolean b) {
        return new String[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getStringArrayConfig(final String s, final ConfigCtx configCtx, final boolean b) {
        return new String[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getIntArray(final String s, final Integer integer) {
        return new int[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getIntArrayConfig(final String s) {
        return new int[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getIntArrayConfig(final String s, final ConfigCtx configCtx) {
        return new int[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] getLongArray(final String s, final Integer integer) {
        return new long[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] getLongArrayConfig(final String s) {
        return new long[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] getLongArrayConfig(final String s, final ConfigCtx configCtx) {
        return new long[0]; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getStringListConfig(final String s) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getStringListConfig(final String s, final ConfigCtx configCtx) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getStringSetConfig(final String s, final ConfigCtx configCtx) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, String> getStringMapConfig(final String s) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, String> getStringMapConfig(final String s, final ConfigCtx configCtx) {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addResource(final ConfigurationResource configurationResource) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addResource(final ConfigurationResource configurationResource, final int i) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ConfigurationResource> getResources() {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setResources(final List<ConfigurationResource> configurationResources) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setCachingPolicy(final ConfigurationCache configurationCache) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ConfigurationCache getCachingPolicy() {
        return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T getConfig(final String key, final ConfigCtx context, final Class<T> resultType) {
        return null;
    }

    @Override
    public <T> T getConfig(final String key, final ConfigCtx context, final Type resultType) {
        return null;
    }

}
