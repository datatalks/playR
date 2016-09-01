package models

import org.joda.time.DateTime


case class OwnerRole(id:  Int, identity: String,  roles: String,  memo:  String, status:Boolean, time: DateTime)

