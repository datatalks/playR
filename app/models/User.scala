package models



case class User(id: Long, firstName: String, lastName: String, mobile: Long, email: String)

case class UserFormData(firstName: String, lastName: String, mobile: Long, email: String)
