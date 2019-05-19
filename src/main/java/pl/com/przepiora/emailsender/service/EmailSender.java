package pl.com.przepiora.emailsender.service;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.com.przepiora.emailsender.model.EmailMessageForm;

@Component
@Slf4j
public class EmailSender implements Runnable{

  private JavaMailSender emailSender;
  private EmailMessageForm emailMessageForm;

  @Autowired
  public EmailSender(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  public void setEmailMessageForm(EmailMessageForm emailMessageForm) {
    this.emailMessageForm = emailMessageForm;
  }

  @Override
  public void run() {
    log.info("Sending e-mail....Thread: {}",Thread.currentThread().getName());
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(emailMessageForm.getTo());
    message.setSubject(emailMessageForm.getSubject());
    message.setText(emailMessageForm.getMessage());
    emailSender.send(message);
    log.info("E-mail was send.");
  }
}
