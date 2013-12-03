package ua.bitternet.docflow.domain

import grails.persistence.Entity;
import groovy.transform.EqualsAndHashCode

/**
 * @author archer
 * Created: Tue Dec 03 18:45:27 EET 2013
 */
@Entity
@EqualsAndHashCode(includes="id")
class Накладна {

  Рахунок рахунокДебет
  Рахунок рахунокКредит

  double сума

  static constraints = {
    рахунокДебет nullable: false
    рахунокКредит nullable: false
    сума nullable: false, empty: false
  }

  def beforeInsert = {
    println 'beforeInsert called'
  }

  def beforeUpdate = {
    println 'beforeUpdate called'
    рахунокДебет.сума -= сума
    рахунокКредит.сума += сума
  }

  public static Накладна новий(args){
    return new Накладна(args)
  }

}