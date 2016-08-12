package models


import org.joda.time.DateTime


case class Identity(id:  Int, identity: String,  password: String,  memo:  String, status:  Boolean, time: DateTime)

case class IdentityFormData(identity: String, password: String)
