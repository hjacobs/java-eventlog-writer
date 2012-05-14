/**
 *
 */
package de.zalando.util.web.urlmapping.util;

import static org.hamcrest.core.IsEqual.equalTo;

import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Helper utility class.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
public class HelperTest {

    @Test
    public void testUrlEncodeSegment() throws Exception {
        assertThat(Helper.urlEncodeSegment("hällo 20% of,the;World"), equalTo("h%e4llo+20%25+of%2cthe%3bWorld"));
    }

}
