package ua.bitternet.docflow.domain

import grails.persistence.Entity;
import groovy.transform.EqualsAndHashCode

/**
 * @author archer
 * Created: Wed Dec 04 10:37:45 EET 2013
 */
@Entity
@EqualsAndHashCode(includes="id")
class РозхіднаНакладна extends Накладна {

  public РозхіднаНакладна(){
    рахунокКредит = Рахунок.новий(0)
  }
}