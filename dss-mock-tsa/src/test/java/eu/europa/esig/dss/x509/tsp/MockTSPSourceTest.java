package eu.europa.esig.dss.x509.tsp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.security.KeyStore.PasswordProtection;
import java.util.Arrays;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.tsp.TimeStampToken;
import org.junit.Test;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.TimestampBinary;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;

public class MockTSPSourceTest {

	@Test
	public void test() throws Exception {
		MockTSPSource mock = new MockTSPSource();

		KeyStoreSignatureTokenConnection token = new KeyStoreSignatureTokenConnection(new File("src/test/resources/self-signed-tsa.p12"), "PKCS12",
				new PasswordProtection("ks-password".toCharArray()));
		mock.setToken(token);
		mock.setAlias("self-signed-tsa");

		byte[] digest = DSSUtils.digest(DigestAlgorithm.SHA256, "Hello".getBytes());
		TimestampBinary timeStampResponse = mock.getTimeStampResponse(DigestAlgorithm.SHA256, digest);

		assertNotNull(timeStampResponse);
		assertNotNull(timeStampResponse.getEncoded());
		assertFalse(Arrays.equals(new byte[] {}, timeStampResponse.getEncoded()));
		CMSSignedData cmsSignedData = new CMSSignedData(timeStampResponse.getEncoded());
		TimeStampToken timeStampToken = new TimeStampToken(cmsSignedData);
		
		assertArrayEquals(digest, timeStampToken.getTimeStampInfo().getMessageImprintDigest());
	}

}
