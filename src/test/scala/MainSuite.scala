import cats.data.EitherT
import munit.FunSuite

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class MainSuite extends FunSuite:
  test("Future.failedで例外を生成する場合"):
    import cats.implicits.*
    import cats.instances.future.*

    import scala.concurrent.ExecutionContext.Implicits.global
    val myFuture: Future[Nothing] = Future.failed(new Exception())
    val f: EitherT[Future, Throwable, String] = myFuture.attemptT
    assert(Await.result(f.isLeft, Duration.Inf))
  test("Futureの中でthrowする場合"):
    import cats.implicits.*
    import cats.instances.future.*

    import scala.concurrent.ExecutionContext.Implicits.global
    val myFuture: Future[Nothing] = Future(throw new Exception())
    val f: EitherT[Future, Throwable, String] = myFuture.attemptT
    assert(Await.result(f.isLeft, Duration.Inf))
  test("Exceptionを生成してtoEitherTする場合"):
    import cats.implicits.*
    import cats.instances.future.*

    import scala.concurrent.ExecutionContext.Implicits.global
    val f: EitherT[Future, Throwable, String] = new Exception().asLeft.toEitherT[Future]
    assert(Await.result(f.isLeft, Duration.Inf))
  test("new Exception().raiseError.attemptT"):
    import cats.implicits.*
    import cats.instances.future.*

    import scala.concurrent.ExecutionContext.Implicits.global
    val future: Future[String] = new Exception().raiseError[Future, String]
    val f: EitherT[Future, Throwable, String] = future.attemptT
    assert(Await.result(f.isLeft, Duration.Inf))

  import java.time.LocalDateTime
  import java.time.format.DateTimeFormatter.*
  val dts = "2018-12-13T19:19"
  LocalDateTime.parse(dts, ISO_DATE_TIME)
