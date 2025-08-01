package bleep

import bleep.internal.{bleepLoggers, FileUtils}
import org.scalactic.TripleEqualsSupport
import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import ryddig.*

import java.nio.file.Files
import java.time.Instant
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

class IntegrationTests extends AnyFunSuite with TripleEqualsSupport {
  val userPaths = UserPaths.fromAppDirs
  val logger0 = Loggers.decodeJsonStream(
    bleepLoggers.BLEEP_JSON_EVENT,
    Loggers
      .stdout(LogPatterns.interface(Some(Instant.now), noColor = false), disableProgress = true)
      .acquire()
      .value
      .withMinLogLevel(LogLevel.info)
  )
  val ec: ExecutionContextExecutor = ExecutionContext.global

  val lazyBleepBuild: Lazy[Started] =
    Lazy {
      val existing = BuildLoader.find(FileUtils.cwd).existing.orThrow
      val pre = Prebootstrapped(logger0, userPaths, BuildPaths(FileUtils.cwd, existing, model.BuildVariant.Normal), existing, ec)
      bootstrap.from(pre, GenBloopFiles.SyncToDisk, Nil, model.BleepConfig.default, CoursierResolver.Factory.default).orThrow
    }

  val prelude =
    s"""$$schema: https://raw.githubusercontent.com/oyvindberg/bleep/master/schema.json
      |$$version: dev
      |jvm:
      |  name: ${model.Jvm.graalvm.name}
      |""".stripMargin

  // note: passing stored log messages is a hack for now. soon commands will return values, and `run` for instance will return printed lines
  def runTest(testName: String, yaml: String, files: Map[RelPath, String])(f: (Started, Commands, TypedLogger[Array[Stored]]) => Assertion): Assertion = {
    val storingLogger = Loggers.storing()
    val stdLogger = logger0.withContext("testName", testName)
    val testTempFolder = Files.createTempDirectory(s"bleep-test-$testName")

    val withBuildScript = files.updated(RelPath.of(BuildLoader.BuildFileName), prelude ++ yaml)
    FileSync.syncStrings(testTempFolder, withBuildScript, FileSync.DeleteUnknowns.No, soft = false).discard()
    val existingBuild = BuildLoader.find(testTempFolder).existing.orThrow
    val buildPaths = BuildPaths(cwd = testTempFolder, existingBuild, model.BuildVariant.Normal)

    try {
      val started = bootstrap
        .from(
          Prebootstrapped(storingLogger.zipWith(stdLogger), userPaths, buildPaths, existingBuild, ec),
          GenBloopFiles.SyncToDiskWith(GenBloopFiles.ReplaceBleepDependencies(lazyBleepBuild)),
          Nil,
          model.BleepConfig.default,
          CoursierResolver.Factory.default
        )
        .orThrow
      val commands = new Commands(started)
      val ret = f(started, commands, storingLogger)
      FileUtils.deleteDirectory(testTempFolder)
      ret
    } finally
      stdLogger.info(s"Ran in $testTempFolder")
  }

  test("run prefer jvmRuntimeOptions") {
    runTest(
      "run prefer jvmRuntimeOptions",
      """projects:
      a:
        platform:
          name: jvm
          jvmRuntimeOptions: -Dfoo=2
          jvmOptions: -Dfoo=1
          mainClass: test.Main
        scala:
          version: 3.4.2
""",
      Map(
        RelPath.force("./a/src/scala/Main.scala") ->
          """package test
        |object Main {
        |  def main(args: Array[String]): Unit =
        |    println("foo was: " + sys.props("foo"))
        |}""".stripMargin
      )
    ) { (_, commands, storingLogger) =>
      commands.run(model.CrossProjectName(model.ProjectName("a"), None))
      assert(storingLogger.underlying.exists(_.message.plainText == "foo was: 2"))
    }
  }
  test("annotation processing disabled by default") {
    runTest(
      "annotation processing disabled by default",
      """projects:
        |  a:
        |    dependencies: org.projectlombok:lombok:1.18.30
        |    source-layout: java
        |    java:
        |      options: -Xlint:all
        |""".stripMargin,
      Map(
        RelPath.force("./a/src/main/java/test/Person.java") ->
          """package test;
            |
            |import lombok.Data;
            |
            |@Data
            |public class Person {
            |    private String name;
            |    private int age;
            |}""".stripMargin
      )
    ) { (started, commands, storingLogger) =>
      val projectName = model.CrossProjectName(model.ProjectName("a"), None)
      val bloopConfig = started.bloopFiles(projectName).forceGet("test")

      // Should have -proc:none in java options
      assert(bloopConfig.project.java.exists(_.options.contains("-proc:none")))
      assert(bloopConfig.project.java.exists(_.options.contains("-Xlint:all")))

      // Generated sources directory should not be in sources
      assert(!bloopConfig.project.sources.exists(_.toString.contains("generated-sources")))
    }
  }

  test("annotation processing enabled") {
    runTest(
      "annotation processing enabled",
      """projects:
        |  a:
        |    dependencies: org.projectlombok:lombok:1.18.30
        |    source-layout: java
        |    java:
        |      options: -Xlint:all
        |      annotationProcessing:
        |        enabled: true
        |""".stripMargin,
      Map(
        RelPath.force("./a/src/main/java/test/Person.java") ->
          """package test;
            |
            |import lombok.Data;
            |
            |@Data
            |public class Person {
            |    private String name;
            |    private int age;
            |}""".stripMargin
      )
    ) { (started, commands, storingLogger) =>
      val projectName = model.CrossProjectName(model.ProjectName("a"), None)
      val bloopConfig = started.bloopFiles(projectName).forceGet("test")

      // Should have -s option with generated sources directory
      val javaOptions = bloopConfig.project.java.map(_.options).getOrElse(Nil)
      assert(javaOptions.exists(_.contains("-s")))
      assert(javaOptions.exists(_.contains("generated-sources")))
      assert(javaOptions.exists(_.contains("annotations")))
      assert(!javaOptions.contains("-proc:none"))
      assert(javaOptions.contains("-Xlint:all"))

      // Generated sources directory should be in sources
      assert(bloopConfig.project.sources.exists(_.toString.contains("generated-sources")))
    }
  }

  test("run fallback to jvmOptions") {
    runTest(
      "run fallback to jvmOptions",
      """projects:
      a:
        platform:
          name: jvm
          jvmOptions: -Dfoo=1
          mainClass: test.Main
        scala:
          version: 3.4.2
""",
      Map(
        RelPath.force("./a/src/scala/Main.scala") ->
          """package test
        |object Main {
        |  def main(args: Array[String]): Unit =
        |    println("foo was: " + sys.props("foo"))
        |}""".stripMargin
      )
    ) { (_, commands, storingLogger) =>
      commands.run(model.CrossProjectName(model.ProjectName("a"), None))
      assert(storingLogger.underlying.exists(_.message.plainText == "foo was: 1"))
    }
  }

  test("resource generator") {
    val bleepYaml = """
projects:
  a:
    extends: common
    platform:
      mainClass: test.Main
    sourcegen: scripts/testscripts.SourceGen
  scripts:
    extends: common
    dependencies: build.bleep::bleep-core:${BLEEP_VERSION}
templates:
  common:
    platform:
      name: jvm
    scala:
      version: 3.4.2
"""

    val Main = """
package test

object Main {
  def main(args: Array[String]): Unit =
    println("result: " + testgenerated.GeneratedSource.result)
}
"""

    val SourceGen = """
package testscripts

import bleep.*
import java.nio.file.Files

object SourceGen extends BleepCodegenScript("SourceGen") {
  def run(started: Started, commands: Commands, targets: List[Target], args: List[String]): Unit = {
    targets.foreach { target =>
      val targetFile = target.sources / "testgenerated" / "GeneratedSource.scala"
      Files.createDirectories(targetFile.getParent)
      Files.writeString(
        targetFile,
        "package testgenerated;\n" +
        "\n" +
        "object GeneratedSource {" +
        "  val result = 100" +
        "}"
      )
      started.logger.withContext("targetFile", targetFile).warn("Wrote ")
    }
  }
}
"""

    runTest(
      "resource generator spike",
      bleepYaml,
      Map(
        RelPath.force("./a/src/scala/test/Main.scala") -> Main,
        RelPath.force("./scripts/src/scala/testscripts/SourceGen.scala") -> SourceGen
      )
    ) { (_, commands, storingLogger) =>
      commands.run(model.CrossProjectName(model.ProjectName("a"), None))
      assert(storingLogger.underlying.exists(_.message.plainText == "result: 100"))
    }
  }
}
