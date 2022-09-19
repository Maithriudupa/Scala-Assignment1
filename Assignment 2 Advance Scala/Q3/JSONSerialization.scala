package Assignment

import lectures.part4implicits.JSONSerialization.{JSONConverter, JSONNumber, JSONObject, JSONString, JSONValue, User}

object JSONSerialization extends App {


  case class User(name: String, age: Int, email: String)

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    def stringify: String =
      "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }
      .mkString("{", ",", "}")

  }

  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConverter[T]): JSONValue =
      converter.convert(value)
  }

  implicit object UserConverter extends JSONConverter[User] {
    def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  val john = User("John", 34, "john@rockthejvm.com")
  println(john.toJSON.stringify)

}
