package com.envision.Staffing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.model.FtpDetails;
import com.envision.Staffing.test.FtpTest;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FtpTestTest {

	FtpDetails ftpDetails = new FtpDetails();

	@InjectMocks
	FtpTest ftpTest;

	@Test
	public void Test() {
		ftpDetails.setFileUrl("http://182.74.103.251:8096/");
		ftpDetails.setUsername("test");
		ftpDetails.setPassword("test");
		ftpDetails.setFileName("testFile.xlsx");
		ftpDetails.setHost("182.74.103.251");

		Assert.assertNotNull(ftpTest.fieldExtraction(ftpDetails, true));
		Assert.assertNotNull(ftpTest.connect(ftpDetails));
		ftpTest.downloadFile("ftp://182.74.103.251/files/test/lol.txt", "test", "test");
		ftpTest.uploadFile("ftp://182.74.103.251/files/test/lol.txt", "test", "test", "/files/test/", "lol.txt");
	}
}
