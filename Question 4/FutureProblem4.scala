package Assignment
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object FutureProblem4 extends App{

  val f1 = Future(Try(1 / 0))
  val f2 = Future(Try(1 / 1))


  def result[A](fa: Future[A], fb: Future[A]): Future[String] = {
    val combinedRes = for {
      ra <- fa
      rb <- fb
    } yield (ra, rb)

    combinedRes.map {
      case (Success(v1), Success(v2)) => s"Both are Successes with result $v1 and $v2"
      case (Success(v1), Failure(f2)) => throw new Exception(s"f2 failed with error -> $f2")
      case (Failure(f1), Success(v2)) => throw new Exception(s"f1 failed with error -> $f1")
      case (Failure(f1), Failure(f2)) => throw new Exception(s"Both are failed with error -> ($f1) and ($f2)")
    }
  }
  println(result(f1,f2))



}
