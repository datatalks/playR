package models


import org.joda.time.DateTime


case class Owner(id:  Int, owner_nickName: String,  owner_realName: String,  password:  String, mobile: Long,
                 email : String, memo : String, status:  Boolean, time: DateTime)

case class OwnerFormData(owner_nickName: String, password: String)
