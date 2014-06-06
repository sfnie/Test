package org.ratchetgx.orion.common.message.impl;

import javax.mail.internet.MimeMessage;

import org.ratchetgx.orion.common.message.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailMessageServiceImpl extends JavaMailSenderImpl implements
		MessageService {

	private static Logger log = LoggerFactory
			.getLogger(MailMessageServiceImpl.class);

	public void send(String sender, String[] address, String title,
			String content) {
		MimeMessage mimeMessage = this.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,
					"UTF-8");
			helper.setFrom(sender);
			helper.setTo(address);
			helper.setSubject(title);
			helper.setText(content, true);
			this.send(mimeMessage);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
