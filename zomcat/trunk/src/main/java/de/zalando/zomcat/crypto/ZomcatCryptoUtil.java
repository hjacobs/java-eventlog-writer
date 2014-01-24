package de.zalando.zomcat.crypto;

import java.io.File;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.keyczar.Crypter;
import org.keyczar.Encrypter;
import org.keyczar.Keyczar;
import org.keyczar.SessionCrypter;

import org.keyczar.exceptions.KeyczarException;

import org.keyczar.interfaces.KeyczarReader;

import org.keyczar.util.Base64Coder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.domain.Environment;

import de.zalando.utils.Pair;

import de.zalando.zomcat.configuration.AppInstanceContextProvider;

/**
 * Offers a simple API for encrypting and decrypting arbitrary strings. It uses Keyczar as cryptography library and
 * assumes that the keys are located in zomcat-keys/{public,private}/{environment}/{projectName}/{keyFiles} under the
 * current working directory, e.g. /data/zalando/app/p0100/zomcat-keys/private/release-staging/shop/1. The underlying
 * algorithm is a combination of RSA and AES.
 *
 * @author  mjuenemann
 */
public class ZomcatCryptoUtil {

    /**
     * The encrypted text returned by this class has the format encryptedText + SEPARATOR + encryptedSymmetricKey. It
     * must not be a character used by Base64Coder. ~ was chosen because it is "web safe", i.e. it will not be encoded
     * when it occurs in an URL
     */
    private static final char SEPARATOR = '~';

    private static final String PRIVATE_KEY_PATH = "zomcat-keys/private";
    private static final String PUBLIC_KEY_PATH = "zomcat-keys/public";

    private final ConcurrentMap<Pair<Environment, String>, Crypter> privateKeyCache = Maps.newConcurrentMap();

    /**
     * Encrypts an arbitrary string using the public key of the given project.
     *
     * @param   text               The text to encrypt
     * @param   targetProject      The name of the project that should be able to decrypt the text (name according to
     *                             deployctl)
     * @param   targetEnvironment  The environment on which the project should be able to decrypt the text
     *
     * @return  The encrypted text. It will only contain the characters [A-Za-z0-9~_-] and can only be decrypted with
     *          the private key of the target project
     *
     * @throws  ZomcatCryptoException  If something went wrong. Most likely reason is that the public key of the target
     *                                 key could not be found
     *
     * @see     {@link AppInstanceContextProvider} if you want to find out the project name and environment of the
     *          current project
     */
    public String encrypt(final String text, final String targetProject, final Environment targetEnvironment)
        throws ZomcatCryptoException {
        try {
            return encrypt(text, new Encrypter(getKeyLocation(PUBLIC_KEY_PATH, targetEnvironment, targetProject)));
        } catch (UnsupportedEncodingException | KeyczarException e) {
            throw new ZomcatCryptoException(String.format("Could not encrypt text for %s-%s", targetProject,
                    targetEnvironment.toString()), e);
        }
    }

    /**
     * Encrypts an arbitrary string using the public key returned by the given KeyczarReader. This method exists mainly
     * for testing purposes, where you don't want to read the keys from the actual file system
     *
     * @param   text       The text to encrypt
     * @param   keyReader  A KeyczarReader that returns the key for encrypting the text
     *
     * @return  The encrypted text. It will only contain the characters [A-Za-z0-9~_-] and can only be decrypted with
     *          the private key of the target project
     *
     * @throws  ZomcatCryptoException  If something went wrong
     */
    public String encrypt(final String text, final KeyczarReader keyReader) throws ZomcatCryptoException {
        try {
            return encrypt(text, new Encrypter(keyReader));
        } catch (UnsupportedEncodingException | KeyczarException e) {
            throw new ZomcatCryptoException("Could not encrypt text using custom KeyczarReader", e);
        }
    }

    private String encrypt(final String text, final Encrypter encrypter) throws KeyczarException,
        UnsupportedEncodingException {
        final SessionCrypter sessionCrypter = new SessionCrypter(encrypter);
        final String msg = Base64Coder.encodeWebSafe(sessionCrypter.encrypt(text.getBytes(Keyczar.DEFAULT_ENCODING)));
        final String key = Base64Coder.encodeWebSafe(sessionCrypter.getSessionMaterial());

        return msg + SEPARATOR + key;
    }

    /**
     * Decrypts a message using the private key of the given project. The private key will be cached to speed up
     * consecutive decryption operations, therefore changing the private key on the file system will have no effect
     * until you create a new instance of ZomcatCryptoUtil
     *
     * @param   message      The message that was returned by the encrypt method. It may only contain the characters
     *                       [A-Za-z0-9~_-]
     * @param   project      The name of the project (according to deployctl) for which the message was encrypted.
     *                       Usually this will be the current project, because the private keys of other projects are
     *                       not available
     * @param   environment  The environment for which the message was encrypted. Usually this will be the current
     *                       environment, because the private keys of other environments are not available
     *
     * @return  The original text as it was passed to the encrypt method
     *
     * @throws  ZomcatCryptoException  If something went wrong. Most likely reason is that the private key could not be
     *                                 found
     *
     * @see     {@link AppInstanceContextProvider} if you want to find out the project name and environment of the
     *          current project
     */
    public String decrypt(final String message, final String project, final Environment environment)
        throws ZomcatCryptoException {
        try {
            Crypter crypter = privateKeyCache.get(Pair.of(environment, project));
            if (crypter == null) {
                crypter = new Crypter(getKeyLocation(PRIVATE_KEY_PATH, environment, project));
                privateKeyCache.put(Pair.of(environment, project), crypter);
            }

            return decrypt(message, crypter);
        } catch (UnsupportedEncodingException | KeyczarException e) {
            throw new ZomcatCryptoException(String.format("Could not decrypt text for %s-%s", project,
                    environment.toString()), e);
        }
    }

    /**
     * Decrypts a message using the private key provided by the given KeyczarReader. This method exists mainly for
     * testing purposes, where you don't want to read the keys from the actual file system
     *
     * @param   message    The message that was returned by the encrypt method. It may only contain the characters
     *                     [A-Za-z0-9~_-]
     * @param   keyReader  A KeyczarReader that returns the key for decrypting the text
     *
     * @return  The original text as it was passed to the encrypt method
     *
     * @throws  ZomcatCryptoException  If something went wrong. Most likely reason is that the private key could not be
     *                                 found
     */
    public String decrypt(final String message, final KeyczarReader keyReader) throws ZomcatCryptoException {
        try {
            return decrypt(message, new Crypter(keyReader));
        } catch (UnsupportedEncodingException | KeyczarException | ZomcatCryptoException e) {
            throw new ZomcatCryptoException("Could not decrypt message using custom KeyczarReader", e);
        }
    }

    private String decrypt(final String msg, final Crypter crypter) throws UnsupportedEncodingException,
        KeyczarException, ZomcatCryptoException {
        final int separatorIndex = msg.indexOf(SEPARATOR);
        if (separatorIndex < 1) {
            throw new ZomcatCryptoException("Invalid message format, can not decrypt");
        }

        final byte[] encryptedMsg = Base64Coder.decodeWebSafe(msg.substring(0, separatorIndex));
        final byte[] encryptedKey = Base64Coder.decodeWebSafe(msg.substring(separatorIndex + 1));
        return new String(new SessionCrypter(crypter, encryptedKey).decrypt(encryptedMsg), Keyczar.DEFAULT_ENCODING);
    }

    private String getKeyLocation(final String path, final Environment environment, final String project) {
        return String.format("%s/%s/%s", path, getEnvironmentDirectoy(environment), project);
    }

    private static String getEnvironmentDirectoy(final Environment environment) {
        return environment.toString().toLowerCase().replace('_', '-');
    }

    /**
     * Returns the names of all projects for which the public keys are available, i.e. which you can use in the encrypt
     * method
     *
     * @param   environment  The target environment
     *
     * @return  List of all project names
     */
    public List<String> getAvailablePublicKeys(final Environment environment) {
        final List<String> publicKeys = Lists.newArrayList();
        final File[] files = new File(PUBLIC_KEY_PATH, getEnvironmentDirectoy(environment)).listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    publicKeys.add(file.getName());
                }
            }
        }

        return publicKeys;
    }

}
