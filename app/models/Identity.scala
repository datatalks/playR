package models

import be.objectify.deadbolt.scala.models.Subject

/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
class Identity (val userName: String, val userRoles: List[SecurityRole]  ) extends Subject {
  override def roles: List[SecurityRole] = userRoles
   // List(SecurityRole("foo"), SecurityRole("bar"))

  //  UserPermission 待后续需要的时候再行使用!!!
  override def permissions: List[UserPermission] =
    List(UserPermission("printers.edit"))

  override def identifier: String = userName
}
