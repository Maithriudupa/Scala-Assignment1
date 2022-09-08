object Retrying extends App {



def retry[T](n: Int)(fn: => T): Unit = {
  try {
    fn
    println(n)
  } catch {
    case e =>
      if (n > 0) retry(n - 1)(fn)
      else println(n)
  }
}

  val plusOne = (x: Int) => 100 / x

  retry(10) {
    plusOne(0)
  }

  retry(10) {
    plusOne(7)
  }
}


