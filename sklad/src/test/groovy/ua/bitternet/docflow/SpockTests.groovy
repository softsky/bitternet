package ua.bitternet.docflow

import ua.bitternet.docflow.domain.*

import ua.bitternet.docflow.service.DataService as БазаДаних
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
  Акт акт = Акт.новий(type: 'Акт', типАкту: "прихідний")

  базаДаних.saveOrFail(акт)
  
  then:
  базаДаних.findAll(Акт).size() == 1
  }

  // ************************************* 
  def "створюємо накладну на переміщення між рахунками"(){
  when:

  def p = new Person(firstName: "archer", lastName: "gutsal")
  базаДаних.saveOrFail(p)

  p.firstName = 'Ludovic'
  базаДаних.saveOrFail(p)

  assert p.firstName == 'Rimero111'
  

  Рахунок дебет = Рахунок.новий(50)
  дебет.сума = 100.0f

  Рахунок кредит = Рахунок.новий(51)
  кредит.сума = 50.0f

  базаДаних.saveOrFail(дебет)
  базаДаних.saveOrFail(кредит)
  
  Накладна накладна = Накладна.новий(рахунокДебет: дебет, рахунокКредит: кредит, сума: 10.0f)

  базаДаних.saveOrFail(накладна)

  println дебет.сума
  println кредит.сума

  базаДаних.saveOrFail(дебет)
  базаДаних.saveOrFail(кредит)

  then:
  базаДаних.findAll(Рахунок)[0].номер == 50
  базаДаних.findAll(Рахунок)[1].номер == 51

  базаДаних.findAll(Рахунок)[0].сума == 90
  базаДаних.findAll(Рахунок)[1].сума == 60
  }
}