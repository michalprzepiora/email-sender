package pl.com.przepiora.emailsender.service;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.com.przepiora.emailsender.model.EmailMessageForm;

@Component
@Log
public class EmailSender {


  private JavaMailSender emailSender;

  @Autowired
  public EmailSender(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  public void sendMessage(EmailMessageForm emailMessageForm){
    log.info("Sending e-mail....");
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(emailMessageForm.getTo());
    message.setSubject(emailMessageForm.getSubject());
    message.setText(emailMessageForm.getMessage());
    emailSender.send(message);
    log.info("E-mail was send.");
  }

}
