package exersise

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike


class BankActorWithTest extends TestKit(ActorSystem("BankActorWithTest"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  // setup
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
  import BankActorWithTest.*
  "Bank Account Transaction" should {
    "send back the status" in {
      val account = system.actorOf(Props(new BankAccount), "bankAccount")
      val person = system.actorOf(Props(new Person), "billionaire")
      person ! LiveTheLife(account)

    }
  }
}
  object BankActorWithTest {

    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object Statement

    case class TransactionSuccess(message: String)

    case class TransactionFailure(reason: String)

    case class LiveTheLife(account: ActorRef)

    class BankAccount extends Actor {


      var funds = 0

      override def receive: Receive = {
        case Deposit(amount) =>
          if (amount < 0) sender() ! TransactionFailure("Invalid deposit amount")
          else {
            funds += amount
            sender() ! TransactionSuccess(s"Successfully deposited $amount")
          }

        case Withdraw(amount) =>
          if (amount < 0) sender() ! TransactionFailure("Invalid withdraw amount")
          else if (amount > funds) sender() ! TransactionFailure("Insufficient Funds")
          else {
            funds -= amount
            sender() ! TransactionSuccess(s"Successfully withdraw $amount")
          }

        case Statement =>
          sender() ! s"Your balance is $funds"
      }
    }


    class Person extends Actor {


      override def receive: Receive = {
        case LiveTheLife(account) =>
          account ! Deposit(10000)
          account ! Withdraw(9000)
          account ! Withdraw(500)
          account ! Statement
          account ! Withdraw(1000)
          account ! Statement
        case message => println(message.toString)
      }

    }
  }
//  import BankAccount._
//    val system = ActorSystem("BankActor")
//    val account = system.actorOf(Props(new BankAccount), "bankAccount")
//    val person = system.actorOf(Props(new Person), "billionaire")
//
//    person ! LiveTheLife(account)


