package Assignment

object AddingImplicittoListScala extends App{

implicit class Trial[A](list: List[A]){
   def print:Unit = for(element <- list) println(element)
  }

val myList = List("Ram", "Sham", 3)
myList.print
}



