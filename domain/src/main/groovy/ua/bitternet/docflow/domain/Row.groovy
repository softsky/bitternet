package ua.bitternet.docflow.domain

import grails.persistence.Entity;

/**
 * Simple domain object representing a pet.
 *
 * @author Arsen A. Gutsal (gutsal.arsen@gmail.com)
 */

@Entity
class Row {

  String name
  Date date

  boolean isNew() { id == null }

  static transients = ['new']
  static belongsTo = [owner: Document]

  static constraints = {
    name blank:false, validator: { val, obj ->
      //if(!obj.id) return "pet.duplicate"
      return true;
    }
  }


}


