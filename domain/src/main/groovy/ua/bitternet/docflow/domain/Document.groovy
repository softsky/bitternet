package ua.bitternet.docflow.domain

import grails.persistence.Entity;
import groovy.transform.EqualsAndHashCode

/**
 * Simple domain object representing an abstract document.
 *
 * @author Arsen A. Gutsal (gutsal.arsen@gmail.com)
 */
@Entity
@EqualsAndHashCode(includes="id")
class Document {

  String type
  Person author
  List<Row> rows = []

  // Test autotimestamp...
  Date dateCreated
  Date lastUpdated

  boolean isNew() { id == null }

  static hasMany = [rows:Row]

  static transients = ['new']

  static constraints = {
    author nullable:true
    type blank:false

    // REQUIRED to pass validation
    dateCreated nullable:true
    lastUpdated nullable:true
  }

  static mapping = {
    rows fetch:'join'
  }

}