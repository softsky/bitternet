package ua.bitternet.docflow.domain

import ua.bitternet.docflow.domain.Document as Документ

import grails.persistence.Entity;

@Entity
public class Акт extends Документ {
  
  String типАкту

  static Акт новий(args){
    return new Акт(args);    
  }

  static mapping = {
    //типАкту type:'text'
  }
}
