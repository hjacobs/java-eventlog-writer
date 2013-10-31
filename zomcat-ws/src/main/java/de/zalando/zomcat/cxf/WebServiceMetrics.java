package de.zalando.zomcat.cxf;

import java.util.EnumMap;

import com.google.common.base.Preconditions;

/**
 * A container for all the metrics collected in a Web Service operation.
 *
 * <p>An instance of this class is used to store metrics values in the <code>Exchange</code> part of a CXF <code>
 * Message</code>.
 *
 * @author  rreis
 * @see     MetricsFields
 */
public class WebServiceMetrics {

    /**
     * A map containing all the metrics stored, by field key.
     *
     * @see  MEtricsFields
     */
    private final EnumMap<MetricsFields, Object> fields;

    /**
     * Constructor of <code>WebServiceMetrics</code>, in a <i>builder</i> pattern.
     *
     * <p>The methods provided by this class are used to build an <code>WebServiceMetrics</code> instance, by chaining
     * its invocations.
     *
     * @author  rreis
     */
    public static class Builder {

        /**
         * A map containing all the values to store in the resulting <code>WebServiceMetrics</code>.
         */
        private EnumMap<MetricsFields, Object> fields = new EnumMap<>(MetricsFields.class);

        /**
         * Returns this object, added with the field specified by the provided key and value inserted.
         *
         * @param   key    The field key.
         * @param   value  The associated value.
         *
         * @return  this object, added with the specified field.
         */
        public Builder field(final MetricsFields key, final Object value) {
            Preconditions.checkNotNull(key, "Key cannot be null");

            fields.put(key, value);
            return this;
        }

        /**
         * Returns this object, added with all the fields provided by the specified <code>WebServiceMetrics</code>
         * object.
         *
         * @param   metrics  the fields to be added to the resulting object.
         *
         * @return  this object, added with all field from the provided <code>WebServiceMetrics</code> object.
         */
        public Builder fromInstance(final WebServiceMetrics metrics) {
            fields.putAll(metrics.fields);
            return this;
        }

        /**
         * Returns a newly instantiated <code>WebServiceMetrics</code> object, with all the fields from this instance.
         *
         * <p>This should be the last method in the building chain.
         *
         * @return  the newly instantiated object.
         */
        public WebServiceMetrics build() {
            return new WebServiceMetrics(this);
        }
    }

    /**
     * Constructs a new instance, with the fields provided by the specified <code>Builder</code>.
     *
     * @param  builder  a <code>Builder</code> with the fields to include.
     */
    private WebServiceMetrics(final Builder builder) {
        fields = builder.fields;
    }

    /**
     * Returns the value associated to the specified key.
     *
     * @param   key  the field to retrieve.
     *
     * @return  the assocaited value, or <code>null</code> if it doesn't exist.
     */
    public Object get(final MetricsFields key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        return fields.get(key);
    }
}
