package de.zalando.zomcat.configuration;

import java.io.IOException;

import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;

import org.junit.Assert;
import org.junit.Test;

import de.zalando.domain.Environment;

public class AppInstanceContextProviderTest {
    @Test
    public void testGetSegmentByHost() {
        AppInstanceContextProvider provider = new AppInstanceContextProvider("http07", null, null);
        Assert.assertEquals(1, provider.getSegment().intValue());

        provider = new AppInstanceContextProvider("fesn05", null, null);
        Assert.assertEquals(2, provider.getSegment().intValue());

        provider = new AppInstanceContextProvider("fesn06", null, null);
        Assert.assertEquals(3, provider.getSegment().intValue());

        provider = new AppInstanceContextProvider("testjapp01a", null, null);
        Assert.assertNull(provider.getSegment());
    }

    @Test
    public void testGetEnvironmentByManifest() throws IOException {
        final String manifestString = "Manifest-Version: 1.0\nX-Environment: release-staging\n";
        Manifest manifest = new Manifest(IOUtils.toInputStream(manifestString));
        Assert.assertEquals(2, manifest.getMainAttributes().size());

        AppInstanceContextProvider provider = new AppInstanceContextProvider(null, null, manifest);
        Assert.assertEquals(Environment.RELEASE_STAGING, provider.getEnvironment());
    }

    @Test
    public void testGetVersion() throws IOException {
        final String manifestString = "Manifest-Version: 1.0\nImplementation-Tag: R13_00_14\n";
        Manifest manifest = new Manifest(IOUtils.toInputStream(manifestString));
        Assert.assertEquals(2, manifest.getMainAttributes().size());

        AppInstanceContextProvider provider = new AppInstanceContextProvider(null, null, manifest);
        Assert.assertEquals("R13_00_14", provider.getVersion());
    }
}
