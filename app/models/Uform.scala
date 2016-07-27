package model


import play.api.data.Form
import play.api.data.Forms._



object UForm {
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UFormData.apply)(UFormData.unapply)
  )
}

case class UFormData(name: String, password: String)
