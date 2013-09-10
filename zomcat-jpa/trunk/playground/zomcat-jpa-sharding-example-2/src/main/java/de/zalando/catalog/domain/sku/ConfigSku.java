package de.zalando.catalog.domain.sku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import de.zalando.catalog.domain.exception.SkuLoadingFailedException;
import de.zalando.catalog.domain.exception.SkuParsingFailedException;
import de.zalando.catalog.domain.sku.SkuFactory.SkuParts;

// @XmlJavaTypeAdapter(ConfigSkuAdapter.class)
public final class ConfigSku extends Sku {

    private static final long serialVersionUID = -2011112115762420858L;

    private static final int LENGTH_SKU = 13;
    public static final int MIN_LENGTH_LEGACY_SKU = 9;
    public static final int MAX_LENGTH_LEGACY_SKU = 18;

    private final ModelSku modelSku;

    private final String configSku;

    private final boolean legacySku;

    private static final Pattern CONFIG_SKU_PATTERN = Pattern.compile("([a-zA-Z\\d]{9})-[a-zA-Z\\d]{3}");

    static final Pattern[] LEGACY_CONFIG_SKU_PATTERNS = {
        Pattern.compile("([a-zA-Z0-9!]{2,3}-[a-zA-Z\\d]{2,4}-[a-zA-Z\\d]{3,5}-\\d{1,2})"),
        Pattern.compile(("([a-zA-Z0-9]{3,6}-[a-zA-Z\\d]{3,4}-\\d{1,6})")),
    };

    //J-
    private static final LoadingCache<String, ConfigSku> CONFIG_SKU_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(16).maximumSize(5000).build(new CacheLoader<String, ConfigSku>() {
                @Override
                public ConfigSku load(final String sku) {
                    final SkuParts skuParts = SkuFactory.parseSku(sku, CONFIG_SKU_PATTERN, LEGACY_CONFIG_SKU_PATTERNS);
                    if (skuParts == null) {
                        throw new SkuParsingFailedException(String.format("invalid config sku [%s]", sku));
                    }
                    return new ConfigSku(skuParts);
                }
            });
    //J+

    private ConfigSku(final SkuParts skuParts) {
        modelSku = ModelSku.valueOf(skuParts.getParentSku(), skuParts.isLegacySku());
        configSku = skuParts.getSku();
        legacySku = skuParts.isLegacySku();
    }

    @Override
    public String asString() {
        return configSku;
    }

    @Override
    public ModelSku getModelSku() {
        return modelSku;
    }

    @Override
    public SkuType getType() {
        return SkuType.CONFIG;
    }

    @Override
    public boolean isLegacySku() {
        return legacySku;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ConfigSku");
        sb.append(" [modelSku=").append(modelSku.asString());
        sb.append(", configSku=").append(configSku);
        sb.append(']');
        return sb.toString();
    }

    public static ConfigSku valueOf(final String configSku) {
        checkConfigSku(configSku);
        try {
            return CONFIG_SKU_CACHE.get(configSku);
        } catch (final UncheckedExecutionException e) {
            if (e.getCause() != null && e.getCause() instanceof SkuParsingFailedException) {
                throw (SkuParsingFailedException) e.getCause();
            } else {
                throw new SkuLoadingFailedException(String.format("failed to load config sku [%s]", configSku), e);
            }
        } catch (final ExecutionException e) {
            throw new SkuLoadingFailedException(String.format("failed to load config sku [%s]", configSku), e);
        }
    }

    public static boolean isValid(final String configSku) {
        try {
            final ConfigSku result = ConfigSku.valueOf(configSku);
            return result != null;
        } catch (final Exception e) {
            return false;
        }
    }

    private static void checkConfigSku(final String configSku) {
        try {
            checkArgument(!isNullOrEmpty(configSku), "config sku must not be blank");
            if (configSku.indexOf('-') == 9) {

                // if the FIRST hyphen comes at position 9
                // then we definitly have a normal sku
                checkArgument(configSku.length() == LENGTH_SKU, "configSku.length() != 13: %s", configSku.length());
            } else {

                // otherwise it is a legacy sku
                checkArgument(configSku.length() <= MAX_LENGTH_LEGACY_SKU, "configSku.length() > 18: %s",
                    configSku.length());
                checkArgument(configSku.length() >= MIN_LENGTH_LEGACY_SKU, "configSku.length() < 9: %s",
                    configSku.length());
            }
        } catch (final IllegalArgumentException e) {
            throw new SkuParsingFailedException(e);
        }
    }

    public static CacheStats stats() {
        return CONFIG_SKU_CACHE.stats();
    }

    @Override
    public Object getShardKey() {
        return getModelSku();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configSku == null) ? 0 : configSku.hashCode());
        result = prime * result + (legacySku ? 1231 : 1237);
        result = prime * result + ((modelSku == null) ? 0 : modelSku.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final ConfigSku other = (ConfigSku) obj;
        if (configSku == null) {
            if (other.configSku != null) {
                return false;
            }
        } else if (!configSku.equals(other.configSku)) {
            return false;
        }

        if (legacySku != other.legacySku) {
            return false;
        }

        if (modelSku == null) {
            if (other.modelSku != null) {
                return false;
            }
        } else if (!modelSku.equals(other.modelSku)) {
            return false;
        }

        return true;
    }
}
