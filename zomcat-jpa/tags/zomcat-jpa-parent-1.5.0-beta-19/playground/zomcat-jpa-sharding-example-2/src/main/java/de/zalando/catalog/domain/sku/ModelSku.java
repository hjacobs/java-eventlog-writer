package de.zalando.catalog.domain.sku;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;

import de.zalando.catalog.domain.exception.SkuLoadingFailedException;
import de.zalando.catalog.domain.exception.SkuParsingFailedException;
import de.zalando.catalog.domain.sku.SkuFactory.SkuParts;

// @XmlJavaTypeAdapter(ModelSkuAdapter.class)
public final class ModelSku extends Sku {

    private static final long serialVersionUID = 3003209238229140362L;

    private static final int LENGTH_SKU = 9;

    private static final Pattern MODEL_SKU_PATTERN = Pattern.compile("([a-zA-Z\\d]{9})");

    private boolean legacySku;

    //J-
    private static final LoadingCache<String, ModelSku> MODEL_SKU_CACHE = CacheBuilder.newBuilder()
            .concurrencyLevel(32).maximumSize(5000).build(new CacheLoader<String, ModelSku>() {
                @Override
                public ModelSku load(final String sku) {
                    final SkuParts skuParts = SkuFactory.parseSku(sku, MODEL_SKU_PATTERN,
                            ConfigSku.LEGACY_CONFIG_SKU_PATTERNS);
                    if (skuParts == null) {
                        throw new SkuParsingFailedException(String.format("invalid model sku [%s]", sku));
                    }
                    return new ModelSku(sku, skuParts.isLegacySku());
                }
            });
    //J+

    private final String modelSku;

    private ModelSku(final String modelSku, final boolean legacySku) {
        this.modelSku = modelSku;
        this.legacySku = legacySku;
    }

    @Override
    public String asString() {
        return modelSku;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ModelSku");
        sb.append(" [modelSku=").append(modelSku);
        sb.append(']');
        return sb.toString();
    }

    public static ModelSku valueOf(final String modelSku) {
        checkModelSku(modelSku);
        try {
            return MODEL_SKU_CACHE.get(modelSku);
        } catch (final ExecutionException e) {
            throw new SkuLoadingFailedException(String.format("failed to load model sku [%s]", modelSku), e);
        }
    }

    /**
     * This method is used internally.
     *
     * @param   modelSku   string
     * @param   legacySku  whether this is legacy or not
     *
     * @return  sku object
     */
    static ModelSku valueOf(final String modelSku, final boolean legacySku) {
        final ModelSku result = ModelSku.valueOf(modelSku);
        result.legacySku = legacySku;
        return result;
    }

    public static boolean isValid(final String modelSku) {
        try {
            final ModelSku result = ModelSku.valueOf(modelSku);
            return result != null;
        } catch (final Exception e) {
            return false;
        }
    }

    private static void checkModelSku(final String modelSku) {
        try {
            checkArgument(!isNullOrEmpty(modelSku), "model sku must not be blank");
            if (modelSku.indexOf('-') == -1) {
                checkArgument(modelSku.length() == LENGTH_SKU);
            } else {
                checkArgument(modelSku.length() <= ConfigSku.MAX_LENGTH_LEGACY_SKU);
                checkArgument(modelSku.length() >= ConfigSku.MIN_LENGTH_LEGACY_SKU);
            }
        } catch (final IllegalArgumentException e) {
            throw new SkuParsingFailedException(e);
        }
    }

    public static CacheStats stats() {
        return MODEL_SKU_CACHE.stats();
    }

    @Override
    public ModelSku getModelSku() {
        return this;
    }

    @Override
    public SkuType getType() {
        return SkuType.MODEL;
    }

    @Override
    public boolean isLegacySku() {
        return legacySku;
    }

    @Override
    public Object getShardKey() {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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

        final ModelSku other = (ModelSku) obj;
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
