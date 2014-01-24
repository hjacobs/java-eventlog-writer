package de.zalando.zomcat.crypto;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import org.keyczar.DefaultKeyType;
import org.keyczar.MockKeyczarReader;

import org.keyczar.enums.KeyPurpose;
import org.keyczar.enums.KeyStatus;

import org.keyczar.exceptions.KeyczarException;

public class ZomcatCryptoUtilTest {
    private static final String SECRET_TEXT = "Hello World! How's it going?!";

    @Test
    public void testEncryption() throws ZomcatCryptoException, KeyczarException {
        final MockKeyczarReader keyReader = new MockKeyczarReader("test", KeyPurpose.DECRYPT_AND_ENCRYPT,
                DefaultKeyType.RSA_PRIV);
        keyReader.addKey(1, KeyStatus.PRIMARY);

        final String encrypted = new ZomcatCryptoUtil().encrypt(SECRET_TEXT, keyReader);
        Assert.assertTrue(Pattern.compile("^[A-Za-z0-9_-]+~[A-Za-z0-9_-]+$").matcher(encrypted).matches());

        Assert.assertEquals(SECRET_TEXT, new ZomcatCryptoUtil().decrypt(encrypted, keyReader));
    }
}
