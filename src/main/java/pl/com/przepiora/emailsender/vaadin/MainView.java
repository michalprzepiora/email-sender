package pl.com.przepiora.emailsender.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.com.przepiora.emailsender.model.EmailMessageForm;
import pl.com.przepiora.emailsender.service.EmailSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route
@Component
public class MainView extends VerticalLayout {

  private TextField to;
  private TextField from;
  private TextField subject;
  private TextArea message;
  private Button send;
  private Button clear;
  private HorizontalLayout sendClearButtons;
  private Label bottom;
  private EmailMessageForm emailMessageForm;

  @Autowired
  private EmailSender emailSender;

  public MainView() {
    initializeComponents();
    ExecutorService exec = Executors.newSingleThreadExecutor();

    send.addClickListener(e -> {
      emailMessageForm = EmailMessageForm.builder()
          .to(to.getValue())
          .from(from.getValue())
          .subject(subject.getValue())
          .message(message.getValue())
          .build();
      emailSender.setEmailMessageForm(emailMessageForm);
      exec.execute(emailSender);
            Notification.show("Email was send.", 3000, Position.MIDDLE);
    });

    clear.addClickListener(e -> {
      Notification.show("Email was send.", 3000, Position.MIDDLE);
    });

    this.add(new H3("On line E-mail sender"));
    this.add(to, from, subject, message, sendClearButtons, bottom);
  }

  private void initializeComponents() {
    this.setMargin(false);
    this.setSpacing(false);
    this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    to = new TextField("To:");
    to.setWidth("50%");
    from = new TextField("From:");
    from.setWidth("50%");
    subject = new TextField("Subject:");
    subject.setWidth("50%");
    message = new TextArea("Message");
    message.setWidth("50%");
    message.setHeight("300px");
    send = new Button("Send");
    clear = new Button("Clear");
    sendClearButtons = new HorizontalLayout(send, clear);
    bottom = new Label("Michał Przepióra - Github: xxxxxx 2019");
    bottom.getStyle().set("font-size", "10px");
  }
}
