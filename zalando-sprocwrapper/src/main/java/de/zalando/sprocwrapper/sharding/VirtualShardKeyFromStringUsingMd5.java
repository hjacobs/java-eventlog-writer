package de.zalando.sprocwrapper.sharding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author  jmussler
 */
public class VirtualShardKeyFromStringUsingMd5 extends VirtualShardKeyStrategy {
    @Override
    public int getShardId(final Object[] objs) {

        String input = (String) objs[0];

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("Unable to use md5 algorithm", nsae);
        }

        StringBuilder buffer = new StringBuilder();

        byte[] md5 = digest.digest(input.getBytes());

        return md5[15] + md5[14] << 8 + md5[13] << 16;
    }
}
