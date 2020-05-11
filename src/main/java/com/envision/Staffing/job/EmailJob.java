package com.envision.Staffing.job;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailJob implements Job {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailProperties mailProperties;

	Logger log = Logger.getLogger(EmailJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
		String subject = jobDataMap.getString("subject");
		String body = jobDataMap.getString("body");
		String recipientEmail = jobDataMap.getString("email");

		sendMail(mailProperties.getUsername(), recipientEmail, subject, body);
	}

	private void sendMail(String fromEmail, String toEmail, String subject, String body) {
		log.info("Entering method for send mail :");
		log.info("From Mail id :" + fromEmail + ", To Mail id :" + toEmail + " , subject for email :" + subject
				+ " , bodyMessage :" + body);
		try {

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);
			messageHelper.setFrom(fromEmail);
			messageHelper.setTo(toEmail);
			mailSender.send(message);
			log.info("Message sent Successfully to " + toEmail);
		} catch (MessagingException ex) {
			log.error("Failed to send email :", ex);
			System.out.println("Failed to send email to {}" + toEmail);
		}
	}

}
