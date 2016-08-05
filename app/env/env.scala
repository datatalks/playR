package env

object env {

   val dir =   if(System.getenv("HOME") == "/root"){"/home/playR"} else { "/Users/datatalks/DT/Dev/playR" }

   val host =  if(System.getenv("HOME") == "/root"){"123.57.162.246"} else { "localhost" }

}
