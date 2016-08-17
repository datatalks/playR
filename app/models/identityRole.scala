package models

import org.joda.time.DateTime


case class IdentityRole(id:  Int, identity: String,  roles: String,  memo:  String,  time: DateTime)

