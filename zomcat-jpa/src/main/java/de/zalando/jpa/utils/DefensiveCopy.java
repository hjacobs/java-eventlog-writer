package de.zalando.jpa.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Helper methods for creating null-safe defensive copies for different mutable data types. Each of these method returns
 * null if the input is null and clones the input otherwise.
 *
 * @author  Sean P. Floyd
 */
public class DefensiveCopy {

    private DefensiveCopy() { }

    public static Date ofDate(final Date input) {
        return input == null ? null : new Date(input.getTime());
    }

    /**
     * Defensive copy of a {@link Set}. If data is null, return null, if it is an {@link EnumSet}, return a clone.
     * Otherwise return a new {@link java.util.HashSet HashSet} with all elements.
     */
    @SuppressWarnings(
        "unchecked"

        // if input is both an EnumSet<?> and a Set<X> we can safely assume that X is an Enum and EnumSet<X>.clone()
        // will also
        // return a Set<X>. The compiler doesn't know that, hence "unchecked".
    )
    public static <X> Set<X> ofSet(final Set<X> input) {
        return input == null
            ? null : input instanceof EnumSet<?> ? (Set<X>) ((EnumSet<?>) input).clone() : Sets.newHashSet(input);
    }

    public static <X> List<X> ofList(final List<X> input) {
        return input == null ? null : Lists.newArrayList(input);
    }

    public static <X, Y> Map<X, Y> ofMap(final Map<X, Y> input) {
        return input == null ? null : Maps.newHashMap(input);
    }

    public static short[] ofShortArray(final short[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

    public static boolean[] ofBooleanArray(final boolean[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

    public static float[] ofFloatArray(final float[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

    public static byte[] ofByteArray(final byte[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

    public static char[] ofCharArray(final char[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

    public static double[] ofDoubleArray(final double[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

    /**
     * Defensive copy for a {@link Set} of {@link Enum Enums} which may or may not be an {@link EnumSet}.
     *
     * @param   set       data, may be null
     * @param   enumType  may not be null
     *
     * @return  if input is null, return null, otherwise return a new {@link EnumSet} with all the supplied elements.
     */
    public static <E extends Enum<E>> EnumSet<E> ofEnumBasedSet(final Set<E> set, @NotNull final Class<E> enumType) {
        return set == null
            ? null
            : set instanceof EnumSet<?> ? ((EnumSet<E>) set).clone()
                                        : set.isEmpty() ? EnumSet.noneOf(enumType) : EnumSet.copyOf(set);
    }

    public static long[] ofLongArray(final long[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

    public static int[] ofIntArray(final int[] input) {
        return input == null ? null : Arrays.copyOf(input, input.length);
    }

}
