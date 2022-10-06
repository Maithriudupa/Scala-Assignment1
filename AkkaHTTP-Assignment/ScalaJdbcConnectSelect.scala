


import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import com.mysql.cj.x.protobuf.MysqlxCrud.Update
import com.typesafe.sslconfig.ssl.AlgorithmConstraintsParser.regex

import java.lang
import scala.concurrent.duration._
import scala.concurrent.Future
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

// step 1
import spray.json._


/**
 * A Scala JDBC connection example by Alvin Alexander,
 * https://alvinalexander.com
 */
import spray.json._
case class USERS(id: Int, name: String, startTime: String)
case object OperationSuccess
trait UserJsonProtocol extends DefaultJsonProtocol {
  implicit val UserFormat = jsonFormat3(USERS)

}
object ScalaJdbcConnectSelect extends App  with UserJsonProtocol with SprayJsonSupport{
  implicit val system = ActorSystem("HighLevelExample")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher
  // connect to the database named "mysql" on the localhost
  val driver = "com.mysql.cj.jdbc.Driver"
  val url = "jdbc:mysql://localhost/Trial"
  val username = "root"
  val password = "password"

  // there's probably a better way to do this
  var connection:Connection = null

  try {
    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    // create the statement, and run the select query


  } catch {
    case e => e.printStackTrace
  }
  val statement = connection.createStatement()
  def getAllUser(UserName: String) = {
    var result: List[USERS] = List()

    val resultSet = "SELECT * FROM USERS WHERE EXISTS (SELECT * FROM USERS WHERE name = ?)"
    val cs = connection.prepareCall(resultSet)
    cs.setString(1, s"$UserName")
    cs.execute
    val cursorResultSet: ResultSet = cs.getResultSet
    while (cursorResultSet.next()) {
      val id = cursorResultSet.getInt("id")
      val name = cursorResultSet.getString("name")
      val startTime = cursorResultSet.getString("startTime")
      result = result :+ USERS(id, name, startTime)
    }
      result
  }

  def  getAllUserBasedOnId(UserId: Int) = {

    var result: List[USERS] = List()
    val resultSet = statement.executeQuery(s"SELECT * FROM USERS where id=$UserId")
    while (resultSet.next()) {
      val id = resultSet.getInt("id")
      val name = resultSet.getString("name")
      val startTime = resultSet.getString("startTime")
      result = result :+ USERS(id, name, startTime)

    }
    result
  }
def passwordValidation(password: Option[String]):String = password match {
  case Some(password) =>
    if (!(password.exists(_.isDigit) && password.length > 8 && password.exists(_.isLetter) && password.exists(!_.isLetterOrDigit))) {
      "Invalid"
    } else password
  case None => ""

}
  def AddingUser(User: USERS, password: Option[String]) = {
    val pass = passwordValidation(password)
    if(pass == "Invalid"){
      StatusCodes.custom(401,"Invalid password format")
    }
else {
      val insertSql =
        """
          |insert into USERS (id, name, startTime, createAt, password)
          |values (?,?,?,?,?)
    """.stripMargin

      val preparedStmt: PreparedStatement = connection.prepareStatement(insertSql)
      preparedStmt.setInt(1, User.id)
      preparedStmt.setString(2, User.name)
      preparedStmt.setString(3, User.startTime)
      preparedStmt.setLong(4, System.currentTimeMillis())
      preparedStmt.setString(5, pass)
      preparedStmt.execute

      preparedStmt.close()

      StatusCodes.OK
    }
  }

  def UpdateUser(User: USERS, UserId: Int, password: Option[String]) = {
    val pass = passwordValidation(password)
    val UpdateSql =
      """
        |UPDATE USERS
        |SET name = ? , startTime= ?, createAt = ?, password = ?
        |WHERE id = ?
    """.stripMargin

    val preparedStmt: PreparedStatement = connection.prepareStatement(UpdateSql)

    preparedStmt.setString(1, User.name)
    preparedStmt.setString(2, User.startTime)
    preparedStmt.setLong(3, System.currentTimeMillis())
    preparedStmt.setString(4, pass)
    preparedStmt.setInt(5, UserId)

    preparedStmt.execute

    preparedStmt.close()

    StatusCodes.OK
  }



  implicit val timeout = Timeout(10 seconds)
  val userServerRoute =
    pathPrefix("api" / "user") {
      get {
        pathEndOrSingleSlash {
          complete(StatusCodes.Unauthorized)
        } ~
          path(Segment) { userName =>
            val users = Future {
              getAllUser(userName)
            }
            val result = users.map(user =>
              HttpEntity(ContentTypes.`application/json`, user.toJson.prettyPrint)
            )
            complete(result)
          }~
          path(IntNumber) { userId =>
            val users = Future {
              getAllUserBasedOnId(userId)
            }
            val result = users.map(user =>

              HttpEntity(ContentTypes.`application/json`,
                user.toJson.prettyPrint)
            )
            println(result)
            complete(result)
          }
      } ~
        post {
          parameters('password.?) { password =>
            entity(as[USERS]) { user =>
              complete(AddingUser(user, password))
            }
          }
        } ~
        patch {
          parameters('id.as[Int], 'password.?) { (id, password) =>
            entity(as[USERS]) { user =>
              complete(UpdateUser(user, id, password))
            }

          }
        }

    }
  Http().bindAndHandle(userServerRoute, "localhost", 8080)
}
