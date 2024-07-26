import cats.*
import cats.effect.{IO, IOApp}
import io.circe
import scribe.*
import scribe.format.Formatter
import scribe.handler.{AsynchronousLogHandle, Overflow}
import software.amazon.awssdk.services.s3.model.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.language.unsafeNulls

val keysJsonFileName = "keys.json"

type KeyList = List[String]

def save(list: List[S3Object]): Unit =
  val keyList: KeyList = list.map(_.key)
  save_(keyList, keysJsonFileName)

def save_(list: List[String], fileName: String): Unit =
  import io.circe.syntax.*
  Files.writeString(Paths.get(fileName), list.asJson.spaces2, StandardCharsets.UTF_8)

def read: Either[circe.Error, KeyList] =
  import io.circe.parser.*
  val string = Files.readString(Paths.get(keysJsonFileName), StandardCharsets.UTF_8)
  parse(string).flatMap(_.as[KeyList])

object DruRun extends IOApp.Simple:
  override def run: IO[Unit] =
    Logger.reset()
    Logger.root.clearHandlers().withMinimumLevel(scribe.Level.Info)
      .withHandler(handle = AsynchronousLogHandle(overflow = Overflow.Error), formatter = Formatter.compact)
      .replace()
    Logger.system.installJUL()
    this.logger.debug("hoge")
    this.logger.withMinimumLevel(Level.Debug).replace()
    this.logger.debug("hoge")
    f(read.fold(throw _, identity), _ => ())
    IO.unit
