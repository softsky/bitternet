package ua.bitternet.docflow.domain
import grails.persistence.Entity;
import groovy.transform.EqualsAndHashCode

/**
 * @author archer
 * Created: Wed Dec 04 10:37:28 EET 2013
 */
@Entity
@EqualsAndHashCode(includes="id")
class ПрихіднаНакладна extends Накладна {
  public ПрихіднаНакладна(){
    рахунокДебет = Рахунок.новий(0)
  }
}