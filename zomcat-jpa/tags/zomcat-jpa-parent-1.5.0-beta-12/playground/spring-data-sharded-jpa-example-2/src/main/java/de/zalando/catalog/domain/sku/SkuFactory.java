package de.zalando.catalog.domain.sku;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SkuFactory {

    private SkuFactory() { }

    static class SkuParts {
        private String sku;

        private String parentSku;

        private boolean legacySku;

        /**
         * @return  the sku
         */
        public String getSku() {
            return sku;
        }

        /**
         * @param  sku  the sku to set
         */
        public void setSku(final String sku) {
            this.sku = sku;
        }

        /**
         * @return  the parentSku
         */
        public String getParentSku() {
            return parentSku;
        }

        /**
         * @param  parentSku  the parentSku to set
         */
        public void setParentSku(final String parentSku) {
            this.parentSku = parentSku;
        }

        /**
         * @return  the legacySku
         */
        public boolean isLegacySku() {
            return legacySku;
        }

        /**
         * @param  legacySku  the legacySku to set
         */
        public void setLegacySku(final boolean legacySku) {
            this.legacySku = legacySku;
        }
    }

    /**
     * Returns the correct sku type. It tries to generate the sku classes in the order simple > config > model. The
     * first one without error is returned.
     *
     * @param   sku
     *
     * @return  correct sku type
     */
    public static Sku valueOf(final String sku) {
        Sku result = null;

        if (ConfigSku.isValid(sku)) {
            result = ConfigSku.valueOf(sku);
        } else {
            if (SimpleSku.isValid(sku)) {
                result = SimpleSku.valueOf(sku);
            } else {
                result = ModelSku.valueOf(sku); // this will always succeed
            }
        }

        return result;
    }

    /**
     * This utility method returns the parts of an sku, if there is no match it returns null. The method should be used
     * only intern, therefore the package default access.
     *
     * @param   sku
     * @param   skuPattern
     * @param   legacyPatterns
     *
     * @return
     */
    static SkuParts parseSku(final String sku, final Pattern skuPattern, final Pattern[] legacyPatterns) {
        SkuParts result = SkuFactory.parseWithPattern(sku, skuPattern);
        if (result != null) {
            return result;
        }

        for (Pattern pattern : legacyPatterns) {
            result = SkuFactory.parseWithPattern(sku, pattern);
            if (result != null) {
                result.setLegacySku(true);
                break;
            }
        }

        return result;
    }

    private static SkuParts parseWithPattern(final String simpleSku, final Pattern pattern) {
        SkuParts result = null;
        Matcher matcher = pattern.matcher(simpleSku);
        if (matcher.matches()) {
            result = new SkuFactory.SkuParts();
            result.setParentSku(matcher.group(1));
            result.setSku(matcher.group(0));
        }

        return result;
    }
}
