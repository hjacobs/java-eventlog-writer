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

public final class SimpleSku extends Sku {

    private static final long serialVersionUID = -4127923033252415158L;

    private static final int LENGTH_SKU = 20;
    private static final int MIN_LENGTH_LEGACY_SKU = 11;
    private static final int MAX_LENGTH_LEGACY_SKU = 24;

    private final ConfigSku configSku;

    private final String simpleSku;

    private final boolean legacySku;

    private static final Pattern SIMPLE_SKU_PATTERN = Pattern.compile("([a-zA-Z\\d]{9}-[a-zA-Z\\d]{3})[a-zA-Z\\d]{7}");

    private static final Pattern[] LEGACY_SIMPLE_SKU_PATTERNS = {
        Pattern.compile("([a-zA-Z\\d]{3}-[a-zA-Z\\d]{3}-\\d{4}-\\d{2})\\d-\\d{3}"),
        Pattern.compile("([a-zA-Z\\d]{5}-[a-zA-Z\\d]{3}-\\d{4})-\\d{1,2}-\\d{1,2}"),
        Pattern.compile("([a-zA-Z\\d]{5}-[a-zA-Z\\d]{3}-\\d{4})[a-zA-Z\\d]{6}"),
        Pattern.compile("([a-zA-Z\\d]{3}-[a-zA-Z\\d]{3}-\\d{4}-\\d{2})[a-zA-Z\\d]{3,4}"),
        Pattern.compile("([a-zA-Z\\d]{5}-[a-zA-Z\\d]{3}-\\d{4})-[smlSML]-[smlSML]"),
        Pattern.compile("([a-zA-Z\\d!]{2,3}-[a-zA-Z\\d]{2,4}-[a-zA-Z\\d]{3,5}-[\\d]{1,2})-[a-zA-Z\\d/,]{1,4}"),
        Pattern.compile(("([a-zA-Z\\d]{3,6}-[a-zA-Z\\d]{3,4}-[\\d]{1,6})-[a-zA-Z\\d/,]{1,5}")),
    };

    //J-
    private static final LoadingCache<String, SimpleSku> SIMPLE_SKU_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(16).maximumSize(5000).build(new CacheLoader<String, SimpleSku>() {
                @Override
                public SimpleSku load(final String sku) {
                    final SkuParts skuParts = SkuFactory.parseSku(sku, SIMPLE_SKU_PATTERN, LEGACY_SIMPLE_SKU_PATTERNS);
                    if (skuParts == null) {
                        throw new SkuParsingFailedException(String.format("invalid simple sku [%s]", sku));
                    }
                    return new SimpleSku(skuParts);
                }
            });
    //J+

    private SimpleSku(final SkuParts skuParts) {
        configSku = ConfigSku.valueOf(skuParts.getParentSku());
        simpleSku = skuParts.getSku();
        legacySku = skuParts.isLegacySku();
    }

    @Override
    public String asString() {
        return simpleSku;
    }

    public ConfigSku getConfigSku() {
        return configSku;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SimpleSku");
        sb.append(" [modelSku=").append(configSku.getModelSku().asString());
        sb.append(", configSku=").append(configSku.asString());
        sb.append(", simpleSku=").append(simpleSku);
        sb.append(']');
        return sb.toString();
    }

    public static SimpleSku valueOf(final String simpleSku) {
        checkSimpleSku(simpleSku);
        try {
            return SIMPLE_SKU_CACHE.get(simpleSku);
        } catch (final UncheckedExecutionException e) {
            if (e.getCause() != null && e.getCause() instanceof SkuParsingFailedException) {
                throw (SkuParsingFailedException) e.getCause();
            } else {
                throw new SkuLoadingFailedException(String.format("failed to load simple sku [%s]", simpleSku), e);
            }
        } catch (final ExecutionException e) {
            throw new SkuLoadingFailedException(String.format("failed to load simple sku [%s]", simpleSku), e);
        }
    }

    public static boolean isValid(final String simpleSku) {
        try {
            final SimpleSku result = SimpleSku.valueOf(simpleSku);
            return result != null;
        } catch (final Exception e) {
            return false;
        }
    }

    private static void checkSimpleSku(final String simpleSku) {
        try {
            checkArgument(!isNullOrEmpty(simpleSku), "simple sku must not be blank");
            if (simpleSku.indexOf('-') == 9) {

                // if the FIRST hyphen comes at position 9
                // then we definitly have a normal sku
                checkArgument(simpleSku.length() == LENGTH_SKU);
            } else {

                // otherwise it is a legacy sku
                checkArgument(simpleSku.length() <= MAX_LENGTH_LEGACY_SKU);
                checkArgument(simpleSku.length() >= MIN_LENGTH_LEGACY_SKU);
            }
        } catch (final IllegalArgumentException e) {
            throw new SkuParsingFailedException(e);
        }
    }

    public static CacheStats stats() {
        return SIMPLE_SKU_CACHE.stats();
    }

    @Override
    public ModelSku getModelSku() {
        return getConfigSku().getModelSku();
    }

    @Override
    public SkuType getType() {
        return SkuType.SIMPLE;
    }

    @Override
    public boolean isLegacySku() {
        return legacySku;
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
        result = prime * result + ((simpleSku == null) ? 0 : simpleSku.hashCode());
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

        final SimpleSku other = (SimpleSku) obj;
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

        if (simpleSku == null) {
            if (other.simpleSku != null) {
                return false;
            }
        } else if (!simpleSku.equals(other.simpleSku)) {
            return false;
        }

        return true;
    }

// @Override
// public boolean equals(final Object o) {
// if (this == o) {
// return true;
// }
//
// if (o == null || getClass() != o.getClass()) {
// return false;
// }
//
// final SimpleSku simpleSku1 = (SimpleSku) o;
//
// return !(simpleSku != null ? !simpleSku.equals(simpleSku1.simpleSku) : simpleSku1.simpleSku != null);
//
// }

// @Override
// public int hashCode() {
// return simpleSku != null ? simpleSku.hashCode() : 0;
// }
}
