package exersise

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import exersise.BankActor.Person.LiveTheLife

object BankActor extends App{
  object BankAccount {

    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object Statement

    case class TransactionSuccess(message: String)

    case class TransactionFailure(reason: String)
  }

  class BankAccount extends Actor {

    import BankAccount._

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

  object Person {
    case class LiveTheLife(account: ActorRef)
  }

  class Person extends Actor {

    import Person._
    import BankAccount._

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
  val system = ActorSystem("BankActor")
  val account = system.actorOf(Props(new BankAccount), "bankAccount")
  val person = system.actorOf(Props(new Person), "billionaire")

  person ! LiveTheLife(account)
}
