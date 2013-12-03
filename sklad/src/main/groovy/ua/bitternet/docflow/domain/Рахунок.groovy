package ua.bitternet.docflow.domain

import grails.persistence.Entity;
import groovy.transform.EqualsAndHashCode

/**
 * @author archer
 * Created: Tue Dec 03 18:41:59 EET 2013
 */
@Entity
@EqualsAndHashCode(includes="id")
class Рахунок {
  int номер
  double сума = 0.0f

  static constraints = {
    номер nullable: false
  }

  static Рахунок новий(int num) {
    return new Рахунок(номер: num)
  }
}