package models


import play.api.data.Form
import play.api.data.Forms._



object RForm {
  val form = Form(
    mapping(
      "owner" -> nonEmptyText,
      "reportR" -> nonEmptyText
    )(RFormData.apply)(RFormData.unapply)
  )
}

case class RFormData(owner: String, reportR: String)
