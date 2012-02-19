package de.zalando.zomcat.cxf.authorization;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutingPattern {

    private static final Logger LOG = LoggerFactory.getLogger(RoutingPattern.class);
    private final Pattern regexPattern;

    public RoutingPattern(final Pattern regexPattern) {
        super();
        this.regexPattern = regexPattern;
    }

    /**
     * Generates a regEx pattern that matches the passed bindingPattern. Valid wildCards in the bindingPattern for
     * substitutions are '*' and '#'.
     *
     * @param   bindingPattern  (like 10.*.20.1, 10.#.1)
     *
     * @return
     */
    public static RoutingPattern compile(String bindingPattern) {

        // if an empty string is  passed, it will be handled as a '#' and a fatal logging is made
        if (isNullOrEmpty(bindingPattern)) {
            LOG.error("the bindingPattern is an empty string and will be set to '#'");
            bindingPattern = "#";
        }

        // escape all '.'
        String regExPattern = bindingPattern.replaceAll("\\.", "\\\\.");

        // replace '*' to a string not containing '.'
        regExPattern = regExPattern.replaceAll("\\*", "[^\\.]+");

        // replace '#' to a string where '.' is allowed
        regExPattern = regExPattern.replaceAll("\\#", ".*");
        regExPattern = '^' + regExPattern + '$';

        RoutingPattern routingPattern = new RoutingPattern(Pattern.compile(regExPattern));
        return routingPattern;
    }

    /**
     * Checks if the input string matches the pattern of the generator.
     *
     * @param   input  (the string to check)
     *
     * @return
     */
    public boolean matches(final String input) {

        if (regexPattern == null) {
            return false;
        }

        Matcher m = regexPattern.matcher(input);
        return m.find();
    }

}
