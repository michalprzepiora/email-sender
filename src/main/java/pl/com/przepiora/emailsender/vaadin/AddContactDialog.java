package pl.com.przepiora.emailsender.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import pl.com.przepiora.emailsender.model.Contact;
import pl.com.przepiora.emailsender.service.ContactService;

public class AddContactDialog extends Dialog {

  private TextField name;
  private TextField surname;
  private TextField email;
  private Button add;
  private Button update;
  private Button cancel;
  private VerticalLayout mainView;
  private HorizontalLayout buttonsRow;
  private Label title;
  private ContactService contactService;


  public AddContactDialog(ContactService contactService) {
    this.contactService = contactService;
    title = new Label("Add new contact:");
    name = new TextField("Name");
    surname = new TextField("Surname");
    email = new TextField("e-mail");
    add = new Button("Add");
    cancel = new Button("Cancel");
    mainView = new VerticalLayout();
    mainView.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    mainView.add(title);
    HorizontalLayout textFieldsRow = new HorizontalLayout();
    textFieldsRow.add(name, surname, email);
    buttonsRow = new HorizontalLayout();
    buttonsRow.add(add, cancel);
    mainView.add(textFieldsRow, buttonsRow);
    add(mainView);
    add.addClickListener(event -> {
      Contact contact = Contact.builder()
          .name(name.getValue())
          .surname(surname.getValue())
          .email(email.getValue())
          .build();
      contactService.addContact(contact);
      this.close();
    });
    cancel.addClickListener(event -> this.close());
  }

  public AddContactDialog(Integer id, String name, String surname, String email,
      ContactService contactService) {
    this(contactService);
    this.name.setValue(name);
    this.surname.setValue(surname);
    this.email.setValue(email);
    this.title.setText("Update contact");
    update = new Button("Update");
    buttonsRow.replace(add, update);
    update.addClickListener(event -> {
      Contact contact = Contact.builder()
          .id(id)
          .name(this.name.getValue())
          .surname(this.surname.getValue())
          .email(this.email.getValue())
          .build();
      contactService.addContact(contact);
      this.close();
    });
  }
}
