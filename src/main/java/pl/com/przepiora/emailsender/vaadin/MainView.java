package pl.com.przepiora.emailsender.vaadin;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.com.przepiora.emailsender.model.Contact;
import pl.com.przepiora.emailsender.model.EmailMessageForm;
import pl.com.przepiora.emailsender.service.ContactService;
import pl.com.przepiora.emailsender.service.EmailSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route
@Component
@UIScope
@Slf4j
public class MainView extends VerticalLayout {

  private ComboBox<Contact> to;
  private TextField from;
  private TextField subject;
  private TextArea message;
  private Button send;
  private Button clear;
  private HorizontalLayout sendClearButtons;
  private Label bottom;
  private EmailMessageForm emailMessageForm;
  private VerticalLayout mainContainer;
  private VerticalLayout homeContainer;
  private VerticalLayout contactsContainer;
  private VerticalLayout aboutContainer;
  private Tabs menu;
  private Grid<Contact> grid;
  private Contact selectedGridItem;
  private Button addContact;
  private Button editContact;
  private Button deleteContact;
  private ExecutorService exec;
  private EmailSender emailSender;
  private ContactService contactService;

  @Autowired
  public MainView(ContactService contactService, EmailSender emailSender) {
    this.emailSender = emailSender;
    this.contactService = contactService;
    initializeComponents();
    exec = Executors.newSingleThreadExecutor();
    add(menu, mainContainer);
    send.addClickListener(e -> sendEmail());
    clear.addClickListener(e -> {
      Notification.show("Panels are cleare now", 3000, Position.MIDDLE);
      clearFields();
    });
  }

  private void sendEmail() {

    emailMessageForm = EmailMessageForm.builder()
        .to(to.getValue().getEmail())
        .from(from.getValue())
        .subject(subject.getValue())
        .message(message.getValue())
        .build();
    try {
      emailSender.setEmailMessageForm(emailMessageForm);
      exec.execute(emailSender);
    } catch (Exception e) {
      Notification.show("Email was not send. Please  try again", 6000, Position.MIDDLE);
    }
    Notification.show("Email was send.", 3000, Position.MIDDLE);
    clearFields();
  }

  private void clearFields() {
    to.clear();
    from.clear();
    subject.clear();
    message.clear();
  }

  private void initializeComponents() {
    this.setMargin(false);
    this.setSpacing(false);
    this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    to = new ComboBox<>("To");
    to.setWidth("50%");
    to.setItems(contactService.findAll());
    ItemLabelGenerator<Contact> contactItemLabelGenerator = (ItemLabelGenerator<Contact>) contact ->
        contact.getName() + " " + contact.getSurname() + "   (" + contact.getEmail() + ")";
    to.setItemLabelGenerator(contactItemLabelGenerator);
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
    bottom = new Label("Michał Przepióra - Github: v :) 2019");
    bottom.getStyle().set("font-size", "10px");
    mainContainer = new VerticalLayout();
    mainContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    initializeHomeContainer();
    initializeContactsContainer();
    initializeAboutContainer();
    initializeMenuBar();
    mainContainer.add(homeContainer);
  }

  private void initializeAboutContainer() {
    aboutContainer = new VerticalLayout();
    aboutContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    aboutContainer.add(new Label("About"));
  }

  private void initializeContactsContainer() {
    contactsContainer = new VerticalLayout();
    contactsContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    contactsContainer.add(new Label("emails"));
    grid = new Grid<>(Contact.class);
    grid.setItems(contactService.findAll());
    grid.removeColumnByKey("id");
    addContact = new Button("Add");
    editContact = new Button("Edit");
    deleteContact = new Button("Delete");
    HorizontalLayout buttons = new HorizontalLayout(addContact, editContact, deleteContact);
    contactsContainer.add(grid, buttons);
    disableEditAndDeleteButtons();
    //Listeners
    addContact.addClickListener(e -> showDialogToAddContact());
    grid.addSelectionListener(eventSelect -> {
      if (eventSelect.getFirstSelectedItem().isPresent()) {
        selectedGridItem = eventSelect.getFirstSelectedItem().get();
        enableEditAndDeleteButtons();
      } else {
        disableEditAndDeleteButtons();
      }
    });
    deleteContact.addClickListener(event -> {
      contactService.deleteContact(selectedGridItem);
      grid.setItems(contactService.findAll());
      log.info("Contact deleted.");
    });
    editContact.addClickListener(editEvent -> {
      Integer id = selectedGridItem.getId();
      String name = selectedGridItem.getName();
      String surname = selectedGridItem.getSurname();
      String email = selectedGridItem.getEmail();

      Dialog dialog = new AddContactDialog(id, name, surname, email, contactService);
      dialog.open();
      dialog.addDetachListener(ex -> {
        grid.setItems(contactService.findAll());
        log.info("Contact updated.");
      });
    });
  }

  private void enableEditAndDeleteButtons() {
    editContact.setEnabled(true);
    deleteContact.setEnabled(true);
  }

  private void disableEditAndDeleteButtons() {
    editContact.setEnabled(false);
    deleteContact.setEnabled(false);
  }

  private void showDialogToAddContact() {
    Dialog dialog = new AddContactDialog(contactService);
    dialog.open();
    dialog.addDetachListener(ex -> {
      refreshContactsFields();
      log.info("Contact added.");
    });
  }

  private void refreshContactsFields() {
    grid.setItems(contactService.findAll());
    to.setItems(contactService.findAll());
  }

  private void initializeHomeContainer() {
    homeContainer = new VerticalLayout();
    homeContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    homeContainer.add(new H3("On line E-mail sender"));
    homeContainer.add(to, from, subject, message, sendClearButtons, bottom);
  }

  private void initializeMenuBar() {
    Tab homeTab = new Tab("Home");
    Tab contactsTab = new Tab("Contacts");
    Tab aboutTab = new Tab("About");
    menu = new Tabs(homeTab, contactsTab, aboutTab);
    menu.addSelectedChangeListener(e -> {
      mainContainer.removeAll();
      if (menu.getSelectedTab().equals(homeTab)) {
        mainContainer.add(homeContainer);
      } else if (menu.getSelectedTab().equals(contactsTab)) {
        mainContainer.add(contactsContainer);
      } else {
        mainContainer.add(aboutContainer);
      }
    });
  }
}
