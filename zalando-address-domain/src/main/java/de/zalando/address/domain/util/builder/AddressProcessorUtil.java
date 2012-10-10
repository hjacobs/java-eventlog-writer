package de.zalando.address.domain.util.builder;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.newTreeMap;

import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.zalando.utils.Pair;

/**
 * Common utility methods for the address processors.
 *
 * @author  Tamas.Eppel@Zalando.de
 */
public final class AddressProcessorUtil {
    public static final Pattern ROMAN_NUMERALS = Pattern.compile(
            "\\bM{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})\\b", Pattern.CASE_INSENSITIVE);

    private static final String EMPTY = "";

    private static final String SPACE = " ";

    private static final Logger LOG = LoggerFactory.getLogger(AddressProcessorUtil.class);
    ;

    private static final Pattern LEFT_CLEANUP = Pattern.compile("^[^\\p{L}\\d]+");

    private static final Pattern RIGHT_CLEANUP = Pattern.compile("[^\\p{L}()\\d.]+$");

    private static final Pattern NON_DIGIT = Pattern.compile("\\D");

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final Pattern ALL_WHITESPACE = Pattern.compile("^\\s*$");

    private AddressProcessorUtil() { }

    public static Pattern compilePattern(final String pattern, final int... flags) {
        Pattern result;
        if ((flags == null) || (flags.length == 0)) {
            result = Pattern.compile(pattern);
        } else {
            result = Pattern.compile(pattern, combineFlags(flags));
        }

        return result;
    }

    public static ImmutableList<Pair<Pattern, Casing>> compileCasingPatterns(final String[] lowerCasePatterns,
            final String[] upperCasePatterns, final String[] capitalizedPatterns, final int... flags) {

        final List<Pair<Pattern, Casing>> result = Lists.newArrayList();
        if (lowerCasePatterns != null) {
            for (int i = 0; i < lowerCasePatterns.length; i++) {
                result.add(Pair.of(compilePattern(lowerCasePatterns[i], flags), Casing.LOWER));
            }
        }

        if (upperCasePatterns != null) {
            for (int i = 0; i < upperCasePatterns.length; i++) {
                result.add(Pair.of(compilePattern(upperCasePatterns[i], flags), Casing.UPPER));
            }
        }

        if (capitalizedPatterns != null) {
            for (int i = 0; i < capitalizedPatterns.length; i++) {
                result.add(Pair.of(compilePattern(capitalizedPatterns[i], flags), Casing.CAPITALIZED));
            }
        }

        return ImmutableList.copyOf(result);
    }

    /**
     * Compiles an array of regex patterns.
     *
     * @param   patterns
     * @param   flags
     *
     * @return
     */
    public static ImmutableList<Pattern> compilePatterns(final String[] patterns, final int... flags) {
        final int len = (patterns == null) ? 0 : patterns.length;
        final Pattern[] result = new Pattern[len];
        if (patterns != null) {
            for (int i = 0; i < patterns.length; i++) {
                result[i] = compilePattern(patterns[i], flags);
            }
        }

        return ImmutableList.copyOf(result);
    }

    /**
     * Compiles regex patterns for replacing. The first item is the regex, the second item is the replace string.
     *
     * @param   replacePatterns
     * @param   flags
     *
     * @return
     */
    public static ImmutableList<Pair<Pattern, String>> compileReplacePattern(final String[][] replacePatterns,
            final int... flags) {
        final int len = (replacePatterns == null) ? 0 : replacePatterns.length;
        @SuppressWarnings("unchecked")
        final Pair<Pattern, String>[] result = new Pair[len];
        if (replacePatterns != null) {
            for (int i = 0; i < replacePatterns.length; i++) {
                result[i] = Pair.of(compilePattern(replacePatterns[i][0], flags), replacePatterns[i][1]);
            }
        }

        return ImmutableList.copyOf(result);
    }

    private static int combineFlags(final int... flags) {
        int flag = 0;
        if (flags != null) {
            for (int i = 0; i < flags.length; i++) {
                flag |= flags[i];
            }
        }

        return flag;
    }

    /**
     * Extracts the number from the input string. The following algorithm is used:
     * <li>Number is in the middle: number is extracted, the other part of {@code position} is stripped away, the left
     *   over is the street name</li>
     * <li>Otherwise: number is extracted, the left over is the street name</li>
     *
     * @param   streetWithNumber
     * @param   number
     * @param   position
     *
     * @return
     */
    public static Pair<String, String> extractNumber(final String streetWithNumber, final String number,
            final NumberPosition position) {
        final String trimmedStreet = streetWithNumber.trim();
        String streetName = trimmedStreet;

        if (!isNullOrEmpty(number)) {
            final int index;
            if (position == NumberPosition.LEFT) {
                index = trimmedStreet.indexOf(number);
            } else {
                index = trimmedStreet.lastIndexOf(number);
            }

            if (index > -1) {

                // is it first or last?
                if ((index == 0) || ((index + number.length()) == trimmedStreet.length())) {
                    final StringBuilder name = new StringBuilder(trimmedStreet);
                    streetName = name.replace(index, index + number.length(), EMPTY).toString();
                } else {
                    if (position == NumberPosition.LEFT) { // throw away the

                        // right
                        streetName = trimmedStreet.substring(index + number.length());
                    } else { // throw away the left
                        streetName = trimmedStreet.substring(0, index);
                    }
                }
            }
        }

        // cleanup left overs
        Matcher matcher;
        if (position == NumberPosition.LEFT) {
            matcher = LEFT_CLEANUP.matcher(streetName.trim());
        } else {
            matcher = RIGHT_CLEANUP.matcher(streetName.trim());
        }

        streetName = matcher.replaceFirst(EMPTY);

        LOG.debug("extracted street name [{}] and number [{}] from [{}]",
            new Object[] {streetName, number, trimmedStreet});

        return Pair.of(streetName, number);
    }

    public static boolean removeSubstring(final StringBuilder builder, final String substring,
            final boolean safeRemove) {

        return removeSubstring(builder, substring, safeRemove, null);
    }

    /**
     * Removes the substring from the string builder. If safeRemove is true, then if the whole string would be removed
     * it will not touch it.
     *
     * @param   builder
     * @param   substring
     * @param   safeRemove
     * @param   indexToRemove  If not null will remove the substring from the given index; otherwise will match the
     *                         first occurence.
     *
     * @return  whether the substring was removed or not
     */
    public static boolean removeSubstring(final StringBuilder builder, final String substring, final boolean safeRemove,
            final Integer indexToRemove) {
        boolean result = true;

        if (!isNullOrEmpty(substring)) {
            final int index = (indexToRemove == null) ? builder.indexOf(substring) : indexToRemove.intValue();

            if (index > -1) {
                builder.replace(index, index + substring.length(), EMPTY);
            }

            if (safeRemove && ALL_WHITESPACE.matcher(builder).matches()) {
                builder.append(substring);
                result = false;
            }
        }

        return result;
    }

    /**
     * Removes the substring matching the given patterns from the string. It returns a pair of the cleaned string and a
     * join of the removed substrings.
     *
     * @param   string
     * @param   patterns
     *
     * @return
     */
    public static Pair<String, String> removePatterns(final String string, final List<Pattern> patterns) {
        final TreeMap<Integer, String> additionals = newTreeMap();
        final StringBuilder builder = new StringBuilder(string);

        // we try to match each additional pattern
        // if found, then it will be removed from the string
        if (patterns != null) {
            int i = 0;
            for (final Pattern pattern : patterns) {
                final Matcher matcher = pattern.matcher(builder);
                final String additional;
                if (matcher.find()) {
                    if (matcher.groupCount() > 0) {
                        additional = nullToEmpty(matcher.group(1));

                        final boolean traceEnabled = LOG.isTraceEnabled();

                        // save the buffer, just for logging its previous state (if trace is enabled)
                        final StringBuilder savedBuffer = traceEnabled ? new StringBuilder(builder) : null;

                        final boolean removed = removeSubstring(builder, additional, true, matcher.start(1));

                        if (removed) {
                            final Integer index = matcher.start(1);
                            final String value = additionals.get(index);

                            if (traceEnabled) {
                                LOG.trace("removed '{}' with pattern #{} [{}] from '{}'",
                                    new Object[] {additional, i, pattern.pattern(), savedBuffer});
                            }

                            if (isNullOrEmpty(value)) {
                                additionals.put(index, additional.trim());
                            } else {
                                additionals.put(index, String.format("%s %s", value, additional.trim()));
                            }
                        }
                    }
                }

                i++;
            }
        }

        // because the patterns can be found in any order we need to store the
        // index in the
        // original string, so that we can sort the in the appropriate order
        // afterwards
        String additionalString = null;
        if (!additionals.isEmpty()) {
            additionalString = Joiner.on(' ').join(additionals.values());
        }

        return Pair.of(builder.toString(), additionalString);
    }

    /**
     * Finds a number according to given patterns.
     *
     * @param   streetWithNumber
     * @param   patterns
     *
     * @return
     */
    public static String findNumber(final String streetWithNumber, final List<Pattern> patterns) {
        String number = EMPTY;
        if (patterns != null) {
            int i = 0;
            for (final Pattern pattern : patterns) {
                final Matcher matcher = pattern.matcher(streetWithNumber);
                if (matcher.find()) {
                    if (matcher.groupCount() > 0) {
                        number = matcher.group(1);
                    } else {
                        number = matcher.group();
                    }

                    if (number != null) {
                        number = number.trim();
                    }

                    LOG.debug("number [{}] guessed with pattern #{} [{}]", new Object[] {number, i, pattern});

                    break;
                }

                i++;
            }
        }

        return number;
    }

    /**
     * Replaces backslashes with slashes.
     *
     * @param   string
     *
     * @return
     */
    public static String replaceBackSlashesWithSlashes(final String string) {
        return string.replace('\\', '/');
    }

    /**
     * Replaces not allowed characters with a single space. Multiple spaces will be collapsed, and trimmed.
     *
     * @param   string
     * @param   notAllowedChars  patter for not allowed characters
     *
     * @return
     */
    public static String replaceNotAllowedCharsAndTrim(final String string, final Pattern notAllowedChars) {
        final Matcher matcher = notAllowedChars.matcher(string);
        return trimAndCollapse(matcher.replaceAll(SPACE));
    }

    /**
     * Remove all non digit characters. Multiple spaces will be collapsed, and trimmed.
     *
     * @param   string
     *
     * @return
     */
    public static String removeNonDigitsAndTrim(final String string) {
        final Matcher matcher = NON_DIGIT.matcher(string);
        return trimAndCollapse(matcher.replaceAll(EMPTY));
    }

    /**
     * Multiple spaces will be collapsed, and trimmed.
     *
     * @param   string
     *
     * @return
     */
    public static String trimAndCollapse(final String string) {
        String result = null;
        if (string != null) {
            final Matcher matcher = WHITESPACE.matcher(string);
            result = matcher.replaceAll(SPACE).trim();
        }

        return result;
    }

    /**
     * Do a simple replace as with sed: search = "(.*)(.{3})$", replacement= "$1 $2" input = "abcd12x" result = "abcd
     * 12x"
     *
     * @param   search
     * @param   replacement
     * @param   input
     *
     * @return
     */
    public static String simpleReplaceRegexp(final String search, final String replacement, final String input) {

        final Pattern p = Pattern.compile(search);
        final Matcher matcher = p.matcher(input);
        return matcher.replaceAll(replacement);
    }
}
