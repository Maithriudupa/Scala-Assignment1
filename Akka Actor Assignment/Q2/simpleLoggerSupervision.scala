package Assignment

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, Props}

import java.util.Date
import java.io._
import java.nio.file.Files
import java.nio.file.Paths
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.http.scaladsl.model.Uri.Empty

object simpleLoggerSupervision extends App{
  object CloseFile
  object OpenFile
  object EmptyString
  object Open
  object Close
  object Number
  case class Sentence(sentence: String)
  val Info = new File("src/main/log/applicationInfo.log")
  var pw1 = new PrintWriter(new FileWriter(Info, true))
  val Warn = new File("src/main/log/applicationWarn.log")
  var pw2 = new PrintWriter(new FileWriter(Warn, true))

  class Parent extends Actor {
    val child = context.actorOf(Props(new ActorWithLogging), "supervisedChild")

    override def receive: Receive = {
      case "" => child ! EmptyString
      case sentence: String => child ! Sentence
      case Open => child ! OpenFile
      case Close => child ! Close
      case a:Int => child ! Number

    }
  }

  class ActorWithLogging extends Actor with ActorLogging {
    override def preStart(): Unit =
      log.info("supervised child started")
      pw1.append(s"[INFO] [${new Date()}] supervised child started \n")

    override def postStop(): Unit =
      log.info("supervised child stopped")
      pw2.append(s"[WARN] [${new Date()}] supervised child stopped \n")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      log.info(s"supervised actor restarting because of ${reason.getMessage}")
      pw1.append(s"[INFO] [${new Date()}] supervised actor restarting because of ${reason.getMessage} \n")
      pw1.close()
      pw2.close()
    }

    override def postRestart(reason: Throwable): Unit =
      log.info("supervised actor Restarted")
      pw1.append(s"[INFO] [${new Date()}] supervised actor Restarted \n")
    override def receive: Receive = {
      case Number => throw new Exception("can only receive strings")

      case EmptyString => throw new NullPointerException("Sentence is empty")

      case OpenFile =>
        pw1 = new PrintWriter(new FileWriter(Info, true))
        pw2 = new PrintWriter(new FileWriter(Warn, true))

      case CloseFile =>
        pw1.close()
        pw2.close()
        val pathWarn = Paths.get("src/main/log/applicationWarn.log")
        val bytesWarn = Files.size(pathWarn)
        println(bytesWarn)
        val pathInfo = Paths.get("src/main/log/applicationInfo.log")
        val bytesInfo = Files.size(pathInfo)
        println(bytesInfo)
        if (bytesInfo > 100) {
          Info.renameTo(new File(s"src/main/log/applicationInfo.log_${new Date()}"))
        }
        if (bytesWarn > 100) {
          Warn.renameTo(new File(s"src/main/log/applicationWarn.log_${new Date()}"))
        }

      case Sentence(sentence: String)=>
        if (sentence.length > 20) {
          pw2.append(s"[WARN] [${new Date()}] Sentence is too big")
          throw new RuntimeException("Sentence is too big")

        }
        else if (!Character.isUpperCase(sentence(0))) {
          pw2.append(s"[WARN] [${new Date()}] sentence must start with uppercase")
          throw new IllegalArgumentException("sentence must start with uppercase")

        }
        else pw1.append(s"[INFO] [${new Date()}] ${sentence} \n")
        log.info(sentence)

    }
  }
  val system = ActorSystem("SupervisionFaultTolarence")
  val supervisor = system.actorOf(Props(new Parent), "supervisor")
  supervisor ! Open
  supervisor ! ""
  supervisor ! "Hello"
  supervisor ! 9
  supervisor !"12345678900987654321345"
  supervisor ! "dghj"
  supervisor ! Close

}
