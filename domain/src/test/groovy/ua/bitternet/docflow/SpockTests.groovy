package ua.bitternet.docflow

import ua.bitternet.docflow.domain.Person
import ua.bitternet.docflow.domain.Document
import ua.bitternet.docflow.domain.Row
import ua.bitternet.docflow.service.DataService
import ua.bitternet.docflow.utils.GormInterceptorsHelper

import org.springframework.context.ApplicationContext
import org.springframework.validation.FieldError
import org.springframework.context.MessageSource


import grails.spring.BeanBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertEquals

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.*

class SpockTests extends Specification  {

  def dataService
  def msg

  void setup(){
    BeanBuilder beanBuilder = new BeanBuilder()
    beanBuilder.loadBeans("classpath:DomainSpringBeans.groovy")

    ApplicationContext context = beanBuilder.createApplicationContext()

    def sf = context.getBean("sessionFactory")
    GormInterceptorsHelper.initializeInterceptors(sf)

    // Alternative to transactional services would be DomainClass.withTransaction
    dataService = context.getBean("dataService") as DataService    
    msg = context.getBean("messageSource") as MessageSource
  }
  // ************************************* TEST METHODS *************************************
  def "база має бути пуста"(){
  expect:
    dataService.findAll(Person).size() == 0
    dataService.findAll(Document).size() == 0
    dataService.findAll(Row).size() == 0  
  }

  // ************************************* 

  def "створюємо документ і строки"(){
  when:
  dataService.withTransaction {
    Person person = new Person(firstName: "Arsen", lastName:"Gutsal")
    dataService.saveOrFail(person);

    Document doc = new Document(id:1, author: person)

    10.times {
      doc.rows << new Row(name: "name${it}", date: new Date(), owner: doc);
    }
  
    dataService.saveOrFail(doc);
  }

  then:
  dataService.findAll(Row).size() == 10  
  dataService.findAll(Document).size() == 1
  dataService.findAll(Person).size() == 1
  }

  // ************************************* 

  def "удаляємо документ, строки мають бути видалені також"(){
  when:
  dataService.withTransaction {
    Person person = new Person(firstName: "Arsen", lastName:"Gutsal")
    dataService.saveOrFail(person);

    Document doc = new Document(author: person)

    10.times {
      doc.rows << new Row(name: "name${it}", date: new Date(), owner: doc);
    }
  
    dataService.saveOrFail(doc);

    // testing cascade removal
    doc.delete();
  }
  then: 
  dataService.findAll(Person).size() == 1
  dataService.findAll(Document).size() == 0
  dataService.findAll(Row).size() == 0
  }

  // *************************************  

  // @Ignore
  // @FailsWith(org.springframework.dao.DataIntegrityViolationException)
  def "удаляємо персону, документ і строки мають залишитись"(){
  when:
  dataService.withTransaction {

    Person person = new Person(firstName: "Arsen", lastName: "Gutsal")
    dataService.saveOrFail(person);

    Document doc = new Document(id:1, author: person)

    10.times {
      doc.rows << new Row(name: "name${it}", date: new Date(), owner: doc);
    }
  
    dataService.saveOrFail(doc);

    // setting author to null
    doc.author = null;
    dataService.saveOrFail(doc)

    // testing cascade removal
    dataService.delete(person);
  }
  then: 
  dataService.findAll(Person).size() == 0
  dataService.findAll(Document).size() == 1
  dataService.findAll(Row).size() == 10
  }

}