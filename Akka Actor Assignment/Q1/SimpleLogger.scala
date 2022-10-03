

package part2Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import java.util.Date
import java.io._
import java.nio.file.Files
import java.nio.file.Paths


object ActorLoggingDemo extends App {
  val Info = new File("src/main/log/applicationInfo.log")
  var pw1 = new PrintWriter(new FileWriter(Info, true))
  val Warn = new File("src/main/log/applicationWarn.log")
  var pw2 = new PrintWriter(new FileWriter(Warn, true))
object CloseFile
  object OpenFile
  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case a: Int =>
        pw2.write(s"[WARN] [${new Date()}] Only strings are allowed \n")
        log.warning("Only strings are allowed")
      case "" =>
        pw2.write(s"[WARN] [${new Date()}] Empty Strings are not allowed \n")
        log.warning("Empty Strings are not allowed")

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
        if (bytesInfo > 1000) {
          Info.renameTo(new File(s"src/main/log/applicationInfo.log_${new Date()}"))
        }
        if (bytesWarn > 1000) {
          Warn.renameTo(new File(s"src/main/log/applicationWarn.log_${new Date()}"))
        }

      case message =>
        pw1.append(s"[INFO] [${new Date()}] ${message.toString} \n")
        log.info(message.toString)

    }
  }
  val system = ActorSystem("LoggingDemo")
  val simplerActor = system.actorOf(Props(new ActorWithLogging))
  simplerActor ! "Logging a simple message by extending a trait"
  simplerActor ! "Hi"
  simplerActor ! 42
  simplerActor ! 2
  simplerActor ! 6
  simplerActor ! 8
  simplerActor ! 0
  simplerActor ! CloseFile
  simplerActor ! OpenFile
  simplerActor ! 8
  simplerActor ! ""
  simplerActor ! CloseFile




}

