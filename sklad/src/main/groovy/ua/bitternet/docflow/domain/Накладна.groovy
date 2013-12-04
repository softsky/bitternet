package ua.bitternet.docflow.domain

import ua.bitternet.docflow.domain.Document as Документ

import grails.persistence.Entity;
import groovy.transform.EqualsAndHashCode

/**
 * @author archer
 * Created: Tue Dec 03 18:45:27 EET 2013
 */
@Entity
@EqualsAndHashCode(includes="id")
class Накладна extends Документ {

  Рахунок рахунокДебет
  Рахунок рахунокКредит

  double сума

  static constraints = {
    рахунокДебет nullable: false
    рахунокКредит nullable: false
    сума nullable: false, empty: false
  }

  def beforeUpdate = {
    рахунокДебет.сума -= сума
    рахунокКредит.сума += сума
  }
}