package pl.com.przepiora.emailsender.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailMessageForm {
  private String to;
  private String from;
  private String subject;
  private String message;

}
