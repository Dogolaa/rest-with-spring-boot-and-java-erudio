package br.com.erudio.services;

import br.com.erudio.exception.ResourceNotFoundException;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PersonServices {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository personRepository;

    public List<Person> findAll() {
        logger.info("Finding all People!");
        return personRepository.findAll();
    }

    public Person findById(Long id) {
        logger.info("Finding one Person!");

        return personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));
    }

    public Person create(Person person) {
        logger.info("Creating one Person");
        return personRepository.save(person);
    }

    public Person update(Person person) {
        logger.info("Updating one Person");

        Person entity = personRepository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return personRepository.save(person);
    }

    public void delete(Long id) {
        logger.info("Deleting one Person");

        Person entity = personRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));

        personRepository.delete(entity);
    }

}
