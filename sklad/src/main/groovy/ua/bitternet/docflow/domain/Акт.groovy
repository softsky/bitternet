package ua.bitternet.docflow.domain

import ua.bitternet.docflow.domain.Document as Документ
import grails.persistence.Entity;

@Entity
public class Акт extends Документ {
  String типАкту
}
