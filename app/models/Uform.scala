package model


import play.api.data.Form
import play.api.data.Forms._



object RForm {
  val form = Form(
    mapping(
      "rmd" -> nonEmptyText
    )(RFormData.apply)(RFormData.unapply)
  )
}

case class RFormData(rmd: String)
