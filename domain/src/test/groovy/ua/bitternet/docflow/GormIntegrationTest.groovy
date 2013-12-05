/*
 * Copyright 2013 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * Simple integration test
 *
 * @author Yves Zoundi
 */
class GormIntegrationTest {
  private static final Logger LOG = LoggerFactory.getLogger(GormIntegrationTest.class)

  DataService dataService;
  MessageSource msg;

  @Before
  void setUp(){
    BeanBuilder beanBuilder = new BeanBuilder()
    beanBuilder.loadBeans("classpath:DomainSpringBeans.groovy")

    ApplicationContext context = beanBuilder.createApplicationContext()

    def sf = context.getBean("sessionFactory")
    GormInterceptorsHelper.initializeInterceptors(sf)

    // Alternative to transactional services would be DomainClass.withTransaction
    dataService = context.getBean("dataService") as DataService    
    msg = context.getBean("messageSource") as MessageSource

  }

  @Test
  void testPersistence() {                
    dataService.withTransaction {
      Person person = new Person("firstName": "Rimero", "lastName":"Solutions")

      dataService.save(person)

      List<Person> persons = dataService.findAll(Person)

      assertEquals(persons.size(), 1)
      Person persistedPerson = persons[0]

      assertEquals(persistedPerson.firstName, "Rimero")
      assertEquals(persistedPerson.lastName,"Solutions")
    }
  }

  @Test
  void testCascadeOperations() {
    dataService.withTransaction {
      Person person = new Person(firstName: "Arsen", lastName: "Gutsal")

      dataService.save(person);

      Document doc = new Document(id:1, author: person)
  
      10.times {
        doc.rows << new Row(name: "name${it}", amount: it, owner: doc);
      }

      if(dataService.validate(doc))
        dataService.save(doc);
        else {
          // print validation errors
          doc.errors.allErrors.each { FieldError error ->
            LOG.error(msg.getMessage(error, Locale.getDefault()))
          }
        
        }

      def documents = dataService.findAll(Document)
      assert documents.size() == 1: 'Single document should be found'
      assert documents[0].rows.size() == 10: 'Document should contain exactly 10 rows'
      assert documents[0].sum == 45: 'Document\'s sum should be exactly 45'
      assert dataService.findAll(Row).size() == 10: 'Database should contain exactly 10 rows'

      dataService.delete(doc)

      assert dataService.findAll(Row).size() == 0: 'Database should contain exactly 0 rows as parent document was deleted'
    }
  }
}