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

import ua.bitternet.docflow.domain.*
import ua.bitternet.docflow.service.DataService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.validation.FieldError

import grails.spring.BeanBuilder
import ua.bitternet.docflow.utils.GormInterceptorsHelper

/**
 * Application launcher
 *
 * @author Yves Zoundi
 */
class Application {
  private static final Logger LOG = LoggerFactory.getLogger(Application.class)

  static main(args) {
    //Load the spring beans
    BeanBuilder beanBuilder = new BeanBuilder()
    beanBuilder.loadBeans("classpath:DomainSpringBeans.groovy")
    beanBuilder.loadBeans("classpath:SpringBeans.groovy")

    ApplicationContext context = beanBuilder.createApplicationContext()
    MessageSource msg = context.getBean("messageSource") as MessageSource

    // Inject interceptors for hibernate event closures in domain classes and autoTimestamping
    def sf = context.getBean("sessionFactory")
    GormInterceptorsHelper.initializeInterceptors(sf)

    
    // Alternative to transactional services would be DomainClass.withTransaction
    DataService dataService = context.getBean("dataService") as DataService

    // Dummy user objects to persist
    def persons = [
      new Person(firstName:"Arsen", lastName:"Gutsal"),
      new Person(firstName:"Rimero", lastName:"Solutions")
    ]

    LOG.info("About to load users: ${persons}")

    // Save person if validation constraints are met
    persons.each { person ->
      if (dataService.validate(person)) {
        dataService.save(person)
        LOG.info("Successfully saved ${person}")
      }
      else {
        // print validation errors
        person.errors.allErrors.each { FieldError error ->
          LOG.error(msg.getMessage(error, Locale.getDefault()))
        }
      }
    }

    LOG.info ("\n\n1. All Persons:  ${dataService.findAll(Person)}")
    Person p = dataService.findAll(Person)[0]

    LOG.info "\n\nWe try to update the firstName to Ludovic but it should reset to Rimero1"
    // Will be set to reset to Rimero1 by beforeUpdate closure in domain
    // because we check if it is set to Ludovic
    p.firstName = "Ludovic" 
    dataService.save(p)

    LOG.info ("\n\n2. All Persons:  ${dataService.findAll(Person)}")

    Акт акт = new Акт(author: persons[0], типАкту: 'прихідний')

    dataService.saveOrFail(акт);     

    p = new Person(firstName: "archer", lastName: "gutsal")
    dataService.saveOrFail(p)

    p.firstName = 'Ludovic'
    dataService.saveOrFail(p)

    Рахунок дебет = Рахунок.новий(50)
    дебет.сума = 100.0f

    Рахунок кредит = Рахунок.новий(51)
    кредит.сума = 50.0f

    dataService.saveOrFail(дебет)
    dataService.saveOrFail(кредит)
  
    Накладна накладна = Накладна.новий(рахунокДебет: дебет, рахунокКредит: кредит, сума: 10.0f)

    10.times {
      накладна.rows << new Row(name: "name${it}", date: new Date())
    }

    dataService.saveOrFail(накладна)


  }

}
