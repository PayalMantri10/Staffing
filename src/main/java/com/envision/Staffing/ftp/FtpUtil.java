package com.envision.Staffing.ftp;

import com.envision.Staffing.model.FtpDetails;
import com.envision.Staffing.model.Output;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {

	public static FtpDetails fieldExtraction(FtpDetails ftpDetails) {

		String ftpUrl = ftpDetails.getFileUrl();	
		
		// Host Name
		Pattern hostPattern = Pattern.compile("(ftp|sftp)://[^/]*/");
		Matcher hostMatcher = hostPattern.matcher(ftpUrl);
		if (hostMatcher.find()) {
			String match = hostMatcher.group(0);
			match = match.substring(0, match.length() - 1);
			match = match.replaceFirst("ftp://", "");
			ftpDetails.setHost(match);
		}

		// Directory Name
		Pattern dirPattern = Pattern.compile("[/][^:.]+/");
		Matcher dirMatcher = dirPattern.matcher(ftpUrl);
		if (dirMatcher.find()) {
			ftpDetails.setDirPath(dirMatcher.group(0));
		}
		
		// File Name 
		Pattern fileNamePattern = Pattern.compile("[/][^/:.]+[.][^:/.0-9]+");
		Matcher fileMatcher = fileNamePattern.matcher(ftpUrl);
		while (fileMatcher.find()) {
			ftpDetails.setFileName(fileMatcher.group(0));
		}
		ftpDetails.setFileName(ftpDetails.getFileName().substring(1));
			
		return ftpDetails;
	}

	public static FTPClient connect(FtpDetails ftpDetails) {
		FTPClient ftp = new FTPClient();
		String host = ftpDetails.getHost();
		Integer port = 21; // ***
		String username = ftpDetails.getUsername();
		String password = ftpDetails.getPassword();

		try {
//			ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
			int reply;
			ftp.connect(host, port);
			ftp.login(username, password);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("GBK");
			ftp.setCharset(Charset.forName("GBK"));

			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ftp;
	}

	public static InputStream downloadFile(FtpDetails ftpDetails) {
		ftpDetails = FtpUtil.fieldExtraction(ftpDetails);
		
		String dirPath = ftpDetails.getDirPath();
		String fileName = ftpDetails.getFileName();
		FTPClient ftp = connect(ftpDetails);
		
		InputStream in = null;
		if (ftp.isConnected()) {
			try {
				in = ftp.retrieveFileStream(dirPath + fileName);
//				System.out.println("FTP File downloaded successfully");
				
				if (ftp.isConnected()) {
					ftp.logout();
					ftp.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return in;
	}

	public static boolean uploadFile(FtpDetails ftpDetails, String jsonString) {
		ftpDetails = FtpUtil.fieldExtraction(ftpDetails);
		
		String remoteDirPath = ftpDetails.getDirPath();
		String remoteFileName = ftpDetails.getFileName();
		
		boolean flag = false;

		FTPClient ftp = connect(ftpDetails);
		if (ftp.isConnected()) {
			try {				
				
				ObjectOutputStream oos = new ObjectOutputStream(ftp.storeFileStream(remoteDirPath + remoteFileName));
				oos.writeBytes(jsonString);
				
				oos.close();
				flag = true;

				ftp.logout();
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
}
