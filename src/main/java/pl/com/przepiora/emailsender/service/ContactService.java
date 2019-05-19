package pl.com.przepiora.emailsender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.przepiora.emailsender.model.Contact;
import pl.com.przepiora.emailsender.repository.ContactRepository;

import java.util.List;

@Service
public class ContactService {

  private final ContactRepository contactRepository;

  @Autowired
  public ContactService(ContactRepository contactRepository) {
    this.contactRepository = contactRepository;
  }

  public void addContact(Contact contact) {
    contactRepository.save(contact);
  }

  public List<Contact> findAll() {
    return contactRepository.findAll();
  }

  public void deleteContact(Contact contact){
    contactRepository.delete(contact);
  }
}
