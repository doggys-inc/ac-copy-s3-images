import cats.*
import cats.data.{NonEmptyList, NonEmptyMap}
import cats.effect.{IO, IOApp}
import cats.implicits.*
import scribe.*
import scribe.format.Formatter
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable
import software.amazon.awssdk.services.sts.StsClient
import spire.implicits.r
import spire.math.Rational

import scala.jdk.CollectionConverters.*
import scala.language.unsafeNulls

def progress[A](numerator: Int, list: NonEmptyList[A]): String = _progress(numerator, list.length)

def _progress(numerator: Int, denominator: Int): String =
  val formattedNumerator = s"%0${denominator.toString.length}d".format(numerator)
  val percentage: Float = (r"100" * Rational(numerator) / Rational(denominator)).toFloat
  val formattedPercentage = percentage.toInt
  s"$formattedNumerator/${denominator.toString} ($formattedPercentage%)"

def msg(key: String, skipOrCopy: String, numerator: Int, denominator: Int) =
  s"${_progress(numerator, denominator)}\t$skipOrCopy\toriginal key: $key"

val extension = "jpg"

val prefix = s"-h.$extension"

def f(keys: List[String], copy: String => Unit): Unit =
  this.logger.withMinimumLevel(Level.Info).replace()
  import scala.collection.parallel.CollectionConverters.*
  val keysLength: Int = keys.length
  this.logger.info(s"keys.length: $keysLength")
  val keyNem: NonEmptyMap[String, NonEmptyList[String]] = keys.toNel
    .fold[NonEmptyList[String]](throw new NoSuchElementException("None.get"))(identity)
    .groupByNem(_.take(2))
  keyNem.keys.toList.foreach(s => this.logger.debug(s"list map of key: $s"))
  keys.zipWithIndex.par.foreach { case (key, i) =>
    key match
      case s if s.endsWith(prefix) =>
        if i % 10000 === 0 then this.logger.debug(msg(key, "skipped", i, keysLength)) else ()
      case s"$a.jpg" => //
        keyNem.lookup(a.take(2)).fold(throw new IllegalStateException("NonEmptyMap is broken.")) { nel =>
          if nel.contains_(s"$a$prefix") then
            if i % 1000 === 0 then this.logger.debug(msg(key, "skipped", i, keysLength)) else ()
          else
            copy(key)
            this.logger.info(msg(key, "copied", i, keysLength))
        }
      case key if key.endsWith("/") => this.logger.debug(msg(key, "ignored", i, keysLength))
      case key => this.logger.debug(msg(key, "ignored", i, keysLength))
  }

object Main extends IOApp.Simple:
  private def newKey(key: String): String = key.replace(s".$extension", prefix)

  override def run: IO[Unit] =
    Logger.reset()
    Logger.root.clearHandlers().withMinimumLevel(scribe.Level.Info)
      .withHandler(formatter = Formatter.compact)
      .replace()
    Logger("software.amazon.awssdk.request").withMinimumLevel(scribe.Level.Debug).replace()
    Logger.system.installJUL()
    val accountId: String = ???
    assert(StsClient.builder().build().getCallerIdentity().account() === accountId)
    val sourceBucket: String = ???
    val s3Client: S3Client = S3Client.builder().build()
    val objects: List[S3Object] =
      s3Client.listObjectsV2Paginator(ListObjectsV2Request.builder().bucket(sourceBucket).build())
        .asScala.flatMap(_.contents().asScala).toList
    save(objects)
    f(
      objects.map(_.key),
      key =>
        s3Client.copyObject(
          CopyObjectRequest.builder().sourceBucket(sourceBucket).sourceKey(key).destinationBucket(sourceBucket)
            .destinationKey(newKey(key))
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()
        )
    )
    IO.unit
