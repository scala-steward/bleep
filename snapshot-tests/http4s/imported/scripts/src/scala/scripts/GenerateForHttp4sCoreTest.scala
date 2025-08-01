
package scripts

import bleep.{BleepCodegenScript, Commands, Started}

import java.nio.file.Files

object GenerateForHttp4sCoreTest extends BleepCodegenScript("GenerateForHttp4sCoreTest") {
  override def run(started: Started, commands: Commands, targets: List[Target], args: List[String]): Unit = {
    started.logger.error("This script is a placeholder! You'll need to replace the contents with code which actually generates the files you want")

    targets.foreach { target =>
      if (Set("http4s-core-test@jvm213", "http4s-core-test@jvm3").contains(target.project.value)) {
        val to = target.sources.resolve("org/http4s/FormDataDecoderDoctest.scala")
        started.logger.withContext(target.project).warn(s"Writing $to")
        val content = s"""|package org.http4s
      |
      |import _root_.munit._
      |
      |class `FormDataDecoderDoctest` extends FunSuite {
      |
      |  def sbtDoctestTypeEquals[A](a1: => A)(a2: => A): _root_.scala.Unit = {
      |    val _ = () => (a1, a2)
      |  }
      |  def sbtDoctestReplString(any: _root_.scala.Any): _root_.scala.Predef.String = {
      |    val s = _root_.scala.runtime.ScalaRunTime.replStringOf(any, 1000).init
      |    if (s.headOption == Some('\\\\n')) s.tail else s
      |  }
      |
      |  test("FormDataDecoder.scala:26: FormDataDecoder") {
      |    import cats.syntax.all._
      |
      |    import cats.data._
      |
      |    import org.http4s.FormDataDecoder._
      |
      |    case class Foo(a: String, b: Boolean)
      |
      |    case class Bar(fs: List[Foo], f: Foo, d: Boolean)
      |
      |    implicit val fooMapper: FormDataDecoder[Foo] = (
      |      field[String]("a"),
      |      field[Boolean]("b")
      |    ).mapN(Foo.apply)
      |
      |    val barMapper = (
      |      list[Foo]("fs"),
      |      nested[Foo]("f"),
      |      field[Boolean]("d")
      |    ).mapN(Bar.apply)
      |
      |    //example at line 47: barMapper( ...
      |    sbtDoctestTypeEquals(barMapper(
      |  Map(
      |   "fs[].a" -> Chain("a1", "a2"),
      |   "fs[].b" -> Chain("true", "false"),
      |   "f.a" -> Chain("fa"),
      |   "f.b" -> Chain("false"),
      |   "d" -> Chain("true"))
      |))((barMapper(
      |  Map(
      |   "fs[].a" -> Chain("a1", "a2"),
      |   "fs[].b" -> Chain("true", "false"),
      |   "f.a" -> Chain("fa"),
      |   "f.b" -> Chain("false"),
      |   "d" -> Chain("true"))
      |)): ValidatedNel[ParseFailure, Bar])
      |      assertEquals(sbtDoctestReplString(barMapper(
      |  Map(
      |   "fs[].a" -> Chain("a1", "a2"),
      |   "fs[].b" -> Chain("true", "false"),
      |   "f.a" -> Chain("fa"),
      |   "f.b" -> Chain("false"),
      |   "d" -> Chain("true"))
      |)), "Valid(Bar(List(Foo(a1,true), Foo(a2,false)),Foo(fa,false),true))")
      |  }
      |
      |}""".stripMargin
        Files.createDirectories(to.getParent)
        Files.writeString(to, content)
      }
    }



    targets.foreach { target =>
      if (Set("http4s-core-test@jvm213", "http4s-core-test@jvm3").contains(target.project.value)) {
        val to = target.sources.resolve("org/http4s/HeadersDoctest.scala")
        started.logger.withContext(target.project).warn(s"Writing $to")
        val content = s"""|package org.http4s
      |
      |import _root_.munit._
      |
      |class `HeadersDoctest` extends FunSuite {
      |
      |  def sbtDoctestTypeEquals[A](a1: => A)(a2: => A): _root_.scala.Unit = {
      |    val _ = () => (a1, a2)
      |  }
      |  def sbtDoctestReplString(any: _root_.scala.Any): _root_.scala.Predef.String = {
      |    val s = _root_.scala.runtime.ScalaRunTime.replStringOf(any, 1000).init
      |    if (s.headOption == Some('\\\\n')) s.tail else s
      |  }
      |
      |  test("Headers.scala:95: withContentLength") {
      |    import org.http4s.headers._
      |
      |    val chunked = Headers(
      |      `Transfer-Encoding`(TransferCoding.chunked),
      |      `Content-Type`(MediaType.text.plain))
      |
      |    //example at line 105: chunked.withContentLength(`Content-Length`.unsafeFromLong(10 ...
      |    sbtDoctestTypeEquals(chunked.withContentLength(`Content-Length`.unsafeFromLong(1024)))((chunked.withContentLength(`Content-Length`.unsafeFromLong(1024))): Headers)
      |      assertEquals(sbtDoctestReplString(chunked.withContentLength(`Content-Length`.unsafeFromLong(1024))), "Headers(Content-Length: 1024, Content-Type: text/plain)")
      |
      |    val chunkedGzipped = Headers(
      |      `Transfer-Encoding`(TransferCoding.chunked, TransferCoding.gzip),
      |      `Content-Type`(MediaType.text.plain))
      |
      |    //example at line 111: chunkedGzipped.withContentLength(`Content-Length`.unsafeFrom ...
      |    sbtDoctestTypeEquals(chunkedGzipped.withContentLength(`Content-Length`.unsafeFromLong(1024)))((chunkedGzipped.withContentLength(`Content-Length`.unsafeFromLong(1024))): Headers)
      |      assertEquals(sbtDoctestReplString(chunkedGzipped.withContentLength(`Content-Length`.unsafeFromLong(1024))), "Headers(Content-Length: 1024, Transfer-Encoding: gzip, Content-Type: text/plain)")
      |
      |    val const = Headers(
      |      `Content-Length`(2048),
      |      `Content-Type`(MediaType.text.plain))
      |
      |    //example at line 117: const.withContentLength(`Content-Length`.unsafeFromLong(1024 ...
      |    sbtDoctestTypeEquals(const.withContentLength(`Content-Length`.unsafeFromLong(1024)))((const.withContentLength(`Content-Length`.unsafeFromLong(1024))): Headers)
      |      assertEquals(sbtDoctestReplString(const.withContentLength(`Content-Length`.unsafeFromLong(1024))), "Headers(Content-Length: 1024, Content-Type: text/plain)")
      |  }
      |
      |}""".stripMargin
        Files.createDirectories(to.getParent)
        Files.writeString(to, content)
      }
    }



    targets.foreach { target =>
      if (Set("http4s-core-test@jvm213", "http4s-core-test@jvm3").contains(target.project.value)) {
        val to = target.sources.resolve("org/http4s/HttpVersionDoctest.scala")
        started.logger.withContext(target.project).warn(s"Writing $to")
        val content = s"""|package org.http4s
      |
      |import _root_.munit._
      |
      |class `HttpVersionDoctest` extends FunSuite {
      |
      |  def sbtDoctestTypeEquals[A](a1: => A)(a2: => A): _root_.scala.Unit = {
      |    val _ = () => (a1, a2)
      |  }
      |  def sbtDoctestReplString(any: _root_.scala.Any): _root_.scala.Predef.String = {
      |    val s = _root_.scala.runtime.ScalaRunTime.replStringOf(any, 1000).init
      |    if (s.headOption == Some('\\\\n')) s.tail else s
      |  }
      |
      |  test("HttpVersion.scala:47: render") {
      |    //example at line 50: HttpVersion.`HTTP/1.1`.renderString
      |    
      |      assertEquals(sbtDoctestReplString(HttpVersion.`HTTP/1.1`.renderString), "HTTP/1.1")
      |  }
      |
      |  test("HttpVersion.scala:56: compare") {
      |    //example at line 59: List(HttpVersion.`HTTP/1.0`, HttpVersion.`HTTP/1.1`, HttpVer ...
      |    
      |      assertEquals(sbtDoctestReplString(List(HttpVersion.`HTTP/1.0`, HttpVersion.`HTTP/1.1`, HttpVersion.`HTTP/0.9`).sorted), "List(HTTP/0.9, HTTP/1.0, HTTP/1.1)")
      |  }
      |
      |  test("HttpVersion.scala:152: fromString") {
      |    //example at line 155: HttpVersion.fromString(\\\\"HTTP/1.1\\\\")
      |    
      |      assertEquals(sbtDoctestReplString(HttpVersion.fromString("HTTP/1.1")), "Right(HTTP/1.1)")
      |  }
      |
      |  test("HttpVersion.scala:177: fromVersion") {
      |    //example at line 180: HttpVersion.fromVersion(1, 1)
      |    
      |      assertEquals(sbtDoctestReplString(HttpVersion.fromVersion(1, 1)), "Right(HTTP/1.1)")
      |
      |    //example at line 183: HttpVersion.fromVersion(1, 10)
      |    
      |      assertEquals(sbtDoctestReplString(HttpVersion.fromVersion(1, 10)), "Left(org.http4s.ParseFailure: Invalid HTTP version: major must be <= 9: 10)")
      |  }
      |
      |}""".stripMargin
        Files.createDirectories(to.getParent)
        Files.writeString(to, content)
      }
    }



    targets.foreach { target =>
      if (Set("http4s-core-test@jvm213", "http4s-core-test@jvm3").contains(target.project.value)) {
        val to = target.sources.resolve("org/http4s/MessageDoctest.scala")
        started.logger.withContext(target.project).warn(s"Writing $to")
        val content = s"""|package org.http4s
      |
      |import _root_.munit._
      |
      |class `MessageDoctest` extends FunSuite {
      |
      |  def sbtDoctestTypeEquals[A](a1: => A)(a2: => A): _root_.scala.Unit = {
      |    val _ = () => (a1, a2)
      |  }
      |  def sbtDoctestReplString(any: _root_.scala.Any): _root_.scala.Predef.String = {
      |    val s = _root_.scala.runtime.ScalaRunTime.replStringOf(any, 1000).init
      |    if (s.headOption == Some('\\\\n')) s.tail else s
      |  }
      |
      |  test("Message.scala:142: putHeaders") {
      |    import org.http4s.headers.Accept
      |
      |    val req = Request().putHeaders(Accept(MediaRange.`application/*`))
      |
      |    //example at line 149: req.headers.get[Accept]
      |    
      |      assertEquals(sbtDoctestReplString(req.headers.get[Accept]), "Some(Accept(NonEmptyList(application/*)))")
      |
      |    val req2 = req.putHeaders(Accept(MediaRange.`text/*`))
      |
      |    //example at line 153: req2.headers.get[Accept]
      |    
      |      assertEquals(sbtDoctestReplString(req2.headers.get[Accept]), "Some(Accept(NonEmptyList(text/*)))")
      |  }
      |
      |  test("Message.scala:161: addHeader") {
      |    import org.http4s.headers.Accept
      |
      |    val req = Request().addHeader(Accept(MediaRange.`application/*`))
      |
      |    //example at line 169: req.headers.get[Accept]
      |    
      |      assertEquals(sbtDoctestReplString(req.headers.get[Accept]), "Some(Accept(NonEmptyList(application/*)))")
      |
      |    val req2 = req.addHeader(Accept(MediaRange.`text/*`))
      |
      |    //example at line 173: req2.headers.get[Accept]
      |    
      |      assertEquals(sbtDoctestReplString(req2.headers.get[Accept]), "Some(Accept(NonEmptyList(application/*, text/*)))")
      |  }
      |
      |}""".stripMargin
        Files.createDirectories(to.getParent)
        Files.writeString(to, content)
      }
    }



    targets.foreach { target =>
      if (Set("http4s-core-test@jvm213", "http4s-core-test@jvm3").contains(target.project.value)) {
        val to = target.sources.resolve("org/http4s/QueryOpsDoctest.scala")
        started.logger.withContext(target.project).warn(s"Writing $to")
        val content = s"""|package org.http4s
      |
      |import _root_.munit._
      |
      |class `QueryOpsDoctest` extends FunSuite {
      |
      |  def sbtDoctestTypeEquals[A](a1: => A)(a2: => A): _root_.scala.Unit = {
      |    val _ = () => (a1, a2)
      |  }
      |  def sbtDoctestReplString(any: _root_.scala.Any): _root_.scala.Predef.String = {
      |    val s = _root_.scala.runtime.ScalaRunTime.replStringOf(any, 1000).init
      |    if (s.headOption == Some('\\\\n')) s.tail else s
      |  }
      |
      |  test("QueryOps.scala:59: ++?") {
      |    import org.http4s.implicits._
      |
      |    //example at line 63: uri\\\\"www.scala.com\\\\".++?(\\\\"key\\\\" -> List(\\\\"value1\\\\", \\\\"value2\\\\", \\\\"va ...
      |    sbtDoctestTypeEquals(uri"www.scala.com".++?("key" -> List("value1", "value2", "value3")))((uri"www.scala.com".++?("key" -> List("value1", "value2", "value3"))): Uri)
      |      assertEquals(sbtDoctestReplString(uri"www.scala.com".++?("key" -> List("value1", "value2", "value3"))), "www.scala.com?key=value1&key=value2&key=value3")
      |  }
      |
      |}""".stripMargin
        Files.createDirectories(to.getParent)
        Files.writeString(to, content)
      }
    }



    targets.foreach { target =>
      if (Set("http4s-core-test@jvm213", "http4s-core-test@jvm3").contains(target.project.value)) {
        val to = target.sources.resolve("org/http4s/headers/Transfer-EncodingDoctest.scala")
        started.logger.withContext(target.project).warn(s"Writing $to")
        val content = s"""|package org.http4s.headers
      |
      |import _root_.munit._
      |
      |class `Transfer-EncodingDoctest` extends FunSuite {
      |
      |  def sbtDoctestTypeEquals[A](a1: => A)(a2: => A): _root_.scala.Unit = {
      |    val _ = () => (a1, a2)
      |  }
      |  def sbtDoctestReplString(any: _root_.scala.Any): _root_.scala.Predef.String = {
      |    val s = _root_.scala.runtime.ScalaRunTime.replStringOf(any, 1000).init
      |    if (s.headOption == Some('\\\\n')) s.tail else s
      |  }
      |
      |  test("Transfer-Encoding.scala:53: filter") {
      |    import org.http4s._
      |
      |    val te = `Transfer-Encoding`(TransferCoding.chunked, TransferCoding.gzip)
      |
      |    //example at line 59: te.filter(_ != TransferCoding.chunked)
      |    sbtDoctestTypeEquals(te.filter(_ != TransferCoding.chunked))((te.filter(_ != TransferCoding.chunked)): Option[`Transfer-Encoding`])
      |      assertEquals(sbtDoctestReplString(te.filter(_ != TransferCoding.chunked)), "Some(Transfer-Encoding(NonEmptyList(TransferCoding(gzip))))")
      |
      |    //example at line 61: te.filter(_ => false)
      |    sbtDoctestTypeEquals(te.filter(_ => false))((te.filter(_ => false)): Option[`Transfer-Encoding`])
      |      assertEquals(sbtDoctestReplString(te.filter(_ => false)), "None")
      |  }
      |
      |}""".stripMargin
        Files.createDirectories(to.getParent)
        Files.writeString(to, content)
      }
    }

  }
}