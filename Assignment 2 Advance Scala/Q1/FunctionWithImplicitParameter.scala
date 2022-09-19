package Assignment

object FunctionWithImplicitParameter extends App {

  implicit val multiplier:Int = 3

  def multiply(value: Int)(implicit by: Int) = value * by

  // Function with Implicit parameter
  val result = multiply(10)
  // Function without Implicit parameter
  val result2 = multiply(10)(5)

  // It will print 30 as a result
  println(s"Function with implicit parameter result is $result")

  // It will print 50 as a result
  println(s"Function without implicit parameter result is $result2")
}
