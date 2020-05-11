package com.envision.Staffing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.ftp.FtpUtil;
import com.envision.Staffing.model.FtpDetails;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FtpUtilTest {

	FtpDetails ftpDetails = new FtpDetails();

	@InjectMocks
	FtpUtil ftpUtil;

	@Test
	public void Test() {
		ftpDetails.setFileUrl("http://182.74.103.251:8096/");
		ftpDetails.setUsername("test");
		ftpDetails.setPassword("test");
		ftpDetails.setFileName("testFile.xlsx");
		ftpDetails.setHost("182.74.103.251");

		ftpUtil.downloadFile(ftpDetails);
		Assert.assertNotNull(ftpUtil.fieldExtraction(ftpDetails));
		Assert.assertNotNull(ftpUtil.connect(ftpDetails));
	}
}
