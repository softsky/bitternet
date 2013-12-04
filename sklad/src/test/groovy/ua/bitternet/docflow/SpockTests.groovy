package ua.bitternet.docflow

import ua.bitternet.docflow.domain.*
import ua.bitternet.docflow.domain.Document as Документ

import ua.bitternet.docflow.service.DataService as БазаДаних
import ua.bitternet.docflow.utils.GormInterceptorsHelper

import org.springframework.context.ApplicationContext
import org.springframework.validation.FieldError
import org.springframework.context.MessageSource

import grails.spring.BeanBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertEquals

import org.springframework.transaction.annotation.Transactional

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.*

class SpockTests extends Specification  {

  def базаДаних
  def msg

  void setup(){
    BeanBuilder beanBuilder = new BeanBuilder()
    beanBuilder.loadBeans("classpath:DomainSpringBeans.groovy")

    ApplicationContext context = beanBuilder.createApplicationContext()

    def sf = context.getBean("sessionFactory")
    GormInterceptorsHelper.initializeInterceptors(sf)

    // Alternative to transactional services would be DomainClass.withTransaction
    базаДаних = context.getBean("dataService") as БазаДаних
    msg = context.getBean("messageSource") as MessageSource
  }
  // ************************************* TEST METHODS *************************************
  def "база має бути пуста"(){
  expect:
  базаДаних.findAll(Person).size() == 0
  базаДаних.findAll(Document).size() == 0
  базаДаних.findAll(Row).size() == 0  
  }

  // ************************************* 
  def "створюємо акта"(){
  when:
  базаДаних.withTransaction {
    def p = new Person(firstName: "archer", lastName: "gutsal")
    базаДаних.saveOrFail(p)

    Акт акт = Акт.новий(типАкту: "прихідний", author: p)

    базаДаних.saveOrFail(акт)
  }
  then:
  базаДаних.findAll(Акт).size() == 1
  }

  // ************************************* 
  def "створюємо накладну на переміщення між рахунками"(){
  when:
  Рахунок дебет
  Рахунок кредит
  базаДаних.withTransaction {

    def p = new Person(firstName: "archer", lastName: "gutsal")
    базаДаних.saveOrFail(p)

    p.firstName = 'Ludovic'
    базаДаних.saveOrFail(p)

    assert p.firstName == 'Rimero1'  

    дебет = Рахунок.новий(50)
    дебет.сума = 100.0f

    кредит = Рахунок.новий(51)
    кредит.сума = 50.0f

    дебет.save()
    кредит.save()
  
    Накладна накладна = Документ.новий(Накладна, [рахунокДебет: дебет, рахунокКредит: кредит, сума: 10.0f])

    накладна.save()
  }
  then:
  базаДаних.findAll(Рахунок)[0].номер == 50
  базаДаних.findAll(Рахунок)[1].номер == 51

  базаДаних.findAll(Рахунок)[0].сума == 100.0f - 10.0f
  базаДаних.findAll(Рахунок)[1].сума == 50.0f + 10.0f

  }

  // ********************************** Прихід, Розхід
  def "створюємо прихідну, розхідну накладну"(){
  when:
  Рахунок дебет
  Рахунок кредит 

  базаДаних.withTransaction {

    дебет = Рахунок.новий(50)
    дебет.сума = 100.0f
    дебет.save()

    кредит = Рахунок.новий(51)
    кредит.сума = 50.0f
    кредит.save()


    ПрихіднаНакладна прихіднаНакладна = Документ.новий(ПрихіднаНакладна, [рахунокКредит: кредит])    
    прихіднаНакладна.сума = 20.0f
    прихіднаНакладна.рахунокДебет.save()
    прихіднаНакладна.save()

    РозхіднаНакладна розхіднаНакладна = Документ.новий(РозхіднаНакладна, [рахунокДебет: дебет])
    розхіднаНакладна.сума = 30.0f
    розхіднаНакладна.рахунокКредит.save()
    розхіднаНакладна.save()
  }

  then:
  базаДаних.findAll(Рахунок)[0].номер == 50
  базаДаних.findAll(Рахунок)[1].номер == 51

  базаДаних.findAll(Рахунок)[0].сума == 100.0f - 30.0f
  базаДаних.findAll(Рахунок)[1].сума == 50.0f + 20.0f
  }
}