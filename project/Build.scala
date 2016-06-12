import sbt.Keys._
import sbt._

object Build extends sbt.Build {  
  val pico_event                = "org.pico"        %%  "pico-event"                % "0.0.1-12"

  val specs2_core               = "org.specs2"      %%  "specs2-core"               % "3.7.2"

  implicit class ProjectOps(self: Project) {
    def standard(theDescription: String) = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(description := theDescription)
          .settings(isSnapshot := true)
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "test"): _*)
  }

  lazy val `pico-event-shim-rabbitmq` = Project(id = "pico-event-shim-rabbitmq", base = file("pico-event-shim-rabbitmq"))
      .standard("pico-event shim library for rabbitmq")
      .libs(pico_event)
      .testLibs(specs2_core)

  lazy val all = Project(id = "pico-event-shim-rabbitmq-project", base = file("."))
      .notPublished
      .aggregate(`pico-event-shim-rabbitmq`)
}
